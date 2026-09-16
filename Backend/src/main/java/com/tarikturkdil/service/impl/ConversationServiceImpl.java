package com.tarikturkdil.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarikturkdil.dto.ConversationResponse;
import com.tarikturkdil.dto.MessageResponse;
import com.tarikturkdil.dto.SendMessageRequest;
import com.tarikturkdil.dto.UserSummaryResponse;
import com.tarikturkdil.entity.Conversation;
import com.tarikturkdil.entity.Message;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.repository.ConversationRepository;
import com.tarikturkdil.repository.MessageRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.IConversationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConversationServiceImpl implements IConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // --- 1. KONUŞMA BAŞLAT YA DA VAR OLANI GETİR ---
    @Override
    @Transactional
    public ConversationResponse startOrGetConversation(Long targetUserId) {
        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(targetUserId)) {
            throw new BaseException(new ErrorMessage(MessageType.CANNOT_MESSAGE_YOURSELF, null));
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));

        Conversation conversation = conversationRepository
                .findBetweenUsers(currentUser.getId(), targetUserId)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation();
                    newConversation.setUser1(currentUser);
                    newConversation.setUser2(targetUser);
                    Conversation saved = conversationRepository.save(newConversation);
                    log.info("Yeni konuşma başlatıldı: {} <-> {}", currentUser.getEmail(), targetUser.getEmail());
                    return saved;
                });

        return mapToConversationResponse(conversation, currentUser);
    }

    // --- 2. KONUŞMALARIMI LİSTELE ---
    @Override
    public List<ConversationResponse> getMyConversations() {
        User currentUser = getCurrentUser();

        List<Conversation> conversations = conversationRepository.findAllByUserId(currentUser.getId());

        return conversations.stream()
                .map(conversation -> mapToConversationResponse(conversation, currentUser))
                .sorted((a, b) -> {
                    // Son mesaj zamanı yoksa, konuşmanın oluşturulma zamanını kullan
                    var timeA = a.getLastMessageTime() != null ? a.getLastMessageTime() : a.getCreateTime();
                    var timeB = b.getLastMessageTime() != null ? b.getLastMessageTime() : b.getCreateTime();
                    return timeB.compareTo(timeA); // en yeni en üstte
                })
                .collect(Collectors.toList());
    }

    // --- 3. BİR KONUŞMANIN MESAJLARINI GETİR ---
    @Override
    public List<MessageResponse> getMessages(Long conversationId) {
        User currentUser = getCurrentUser();
        Conversation conversation = getConversationAndVerifyAccess(conversationId, currentUser);

        List<Message> messages = messageRepository.findByConversationIdOrderByCreateTimeAsc(conversation.getId());

        return messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }

    // --- 4. MESAJ GÖNDER (WebSocket katmanı tarafından da kullanılacak) ---
    @Override
    @Transactional
    public MessageResponse sendMessage(Long conversationId, SendMessageRequest input) {
        User currentUser = getCurrentUser();
        Conversation conversation = getConversationAndVerifyAccess(conversationId, currentUser);

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(currentUser);
        message.setContent(input.getContent());
        message.setRead(false);

        Message savedMessage = messageRepository.save(message);
        log.info("Mesaj gönderildi: conversationId={}, gönderen={}", conversationId, currentUser.getEmail());

        return mapToMessageResponse(savedMessage);
    }

    // --- YARDIMCI METOTLAR ---

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }

    // Konuşmayı bul VE giriş yapan kullanıcının bu konuşmanın gerçekten bir
    // tarafı olduğunu doğrula — başkasının özel konuşmasına erişimi engeller.
    private Conversation getConversationAndVerifyAccess(Long conversationId, User currentUser) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CONVERSATION_NOT_FOUND, null)));

        boolean isParticipant = conversation.getUser1().getId().equals(currentUser.getId())
                || conversation.getUser2().getId().equals(currentUser.getId());

        if (!isParticipant) {
            log.warn("Yetkisiz konuşma erişim denemesi: {} kullanıcısı kendine ait olmayan bir konuşmaya erişmeye çalıştı.", currentUser.getEmail());
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, null));
        }

        return conversation;
    }

    private ConversationResponse mapToConversationResponse(Conversation conversation, User currentUser) {
        User otherUser = conversation.getUser1().getId().equals(currentUser.getId())
                ? conversation.getUser2()
                : conversation.getUser1();

        UserSummaryResponse otherUserSummary = new UserSummaryResponse();
        otherUserSummary.setId(otherUser.getId());
        otherUserSummary.setName(otherUser.getName());
        otherUserSummary.setAvatarUrl(otherUser.getAvatarUrl());

        List<Message> messages = messageRepository.findByConversationIdOrderByCreateTimeAsc(conversation.getId());

        String lastMessagePreview = messages.isEmpty() ? null : messages.get(messages.size() - 1).getContent();
        var lastMessageTime = messages.isEmpty() ? null : messages.get(messages.size() - 1).getCreateTime();

        long unreadCount = messageRepository.countByConversationIdAndIsReadFalseAndSenderIdNot(
                conversation.getId(), currentUser.getId());

        ConversationResponse response = new ConversationResponse();
        response.setId(conversation.getId());
        response.setCreateTime(conversation.getCreateTime());
        response.setOtherUser(otherUserSummary);
        response.setLastMessagePreview(lastMessagePreview);
        response.setLastMessageTime(lastMessageTime);
        response.setUnreadCount(unreadCount);

        return response;
    }
    
    @Override
    @Transactional
    public void markMessagesAsRead(Long conversationId) {
        User currentUser = getCurrentUser();
        Conversation conversation = getConversationAndVerifyAccess(conversationId, currentUser);

        List<Message> unread = messageRepository
                .findByConversationIdAndIsReadFalseAndSenderIdNot(conversation.getId(), currentUser.getId());

        unread.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unread);
    }

    private MessageResponse mapToMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setCreateTime(message.getCreateTime());
        response.setSenderId(message.getSender().getId());
        response.setContent(message.getContent());
        response.setRead(message.isRead());
        response.setConversationId(message.getConversation().getId());
        return response;
    }
}