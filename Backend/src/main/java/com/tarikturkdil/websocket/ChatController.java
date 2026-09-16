package com.tarikturkdil.websocket;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional; // YENİ IMPORT

import com.tarikturkdil.dto.MessageResponse;
import com.tarikturkdil.dto.NotificationEvent;
import com.tarikturkdil.dto.SendMessageRequest;
import com.tarikturkdil.entity.Conversation;
import com.tarikturkdil.entity.NotificationType;
import com.tarikturkdil.entity.Pin;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.kafka.NotificationEventProducer;
import com.tarikturkdil.repository.ConversationRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.IConversationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ChatController {

    @Autowired
    private IConversationService conversationService;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private NotificationEventProducer notificationEventProducer;

    @MessageMapping("/chat.send/{conversationId}")
    @Transactional // BUNU EKLE — lazy User/Conversation alanlarına güvenle erişebilmek için
    public void sendMessage(@DestinationVariable Long conversationId,
                             @Payload SendMessageRequest input,
                             Principal principal) {

        String senderEmail = principal.getName();

        setSecurityContext(senderEmail);

        try {
            MessageResponse response = conversationService.sendMessage(conversationId, input);

            Conversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.CONVERSATION_NOT_FOUND, null)));

            User sender = userRepository.findByEmail(senderEmail)
                    .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));

            User recipient = conversation.getUser1().getId().equals(sender.getId())
                    ? conversation.getUser2()
                    : conversation.getUser1();

            messagingTemplate.convertAndSendToUser(recipient.getEmail(), "/queue/messages", response);
            messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/messages", response);
            
            notificationEventProducer.publish(new NotificationEvent(
                    recipient.getId(), sender.getId(), NotificationType.NEW_MESSAGE, conversationId
            ));

            log.info("WebSocket mesajı iletildi: conversationId={}, gönderen={}, alıcı={}",
                    conversationId, senderEmail, recipient.getEmail());

        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void setSecurityContext(String email) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}