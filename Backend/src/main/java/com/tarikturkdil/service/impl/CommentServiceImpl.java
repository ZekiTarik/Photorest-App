package com.tarikturkdil.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarikturkdil.dto.CommentCreateRequest;
import com.tarikturkdil.dto.CommentResponse;
import com.tarikturkdil.dto.NotificationEvent;
import com.tarikturkdil.entity.Comment;
import com.tarikturkdil.entity.NotificationType;
import com.tarikturkdil.entity.Pin;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.kafka.NotificationEventProducer;
import com.tarikturkdil.repository.CommentRepository;
import com.tarikturkdil.repository.PinRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.ICommentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PinRepository pinRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationEventProducer notificationEventProducer;

    // --- 1. YORUM EKLE ---
    @Override
    @Transactional
    public CommentResponse addComment(Long pinId, CommentCreateRequest input) {
        User currentUser = getCurrentUser();

        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PIN_NOT_FOUND, null)));

        Comment comment = new Comment();
        comment.setText(input.getText());
        comment.setUser(currentUser);
        comment.setPin(pin);

        Comment savedComment = commentRepository.save(comment);
        log.info("Yorum eklendi: pinId={}, kullanıcı={}", pinId, currentUser.getEmail());

        if (!pin.getUser().getId().equals(currentUser.getId())) {
            notificationEventProducer.publish(new NotificationEvent(
                    pin.getUser().getId(), currentUser.getId(), NotificationType.NEW_COMMENT, pinId
            ));
        }
        
        return mapToCommentResponse(savedComment);
    }

    // --- 2. PİNİN YORUMLARINI LİSTELE ---
    @Override
    public List<CommentResponse> getCommentsByPin(Long pinId) {
        List<Comment> comments = commentRepository.findByPinIdOrderByCreateTimeDesc(pinId);

        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    // --- 3. YORUM SİL ---
    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        User currentUser = getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.COMMENT_NOT_FOUND, null)));

        // Sadece yorumu yazan kişi silebilir
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            log.warn("Yetkisiz yorum silme denemesi: {} kullanıcısı başkasının yorumunu silmeye çalıştı.", currentUser.getEmail());
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, null));
        }

        commentRepository.delete(comment);
        log.info("Yorum silindi: commentId={}, kullanıcı={}", commentId, currentUser.getEmail());
    }

    // --- YARDIMCI METOTLAR ---

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setCreateTime(comment.getCreateTime());
        response.setText(comment.getText());
        response.setUsername(comment.getUser().getName());
        return response;
    }
}