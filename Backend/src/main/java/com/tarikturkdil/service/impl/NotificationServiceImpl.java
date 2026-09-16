package com.tarikturkdil.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tarikturkdil.dto.NotificationResponse;
import com.tarikturkdil.entity.Notification;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.repository.NotificationRepository;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.INotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationServiceImpl implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = getCurrentUser();

        List<Notification> notifications = notificationRepository
                .findByRecipientIdOrderByCreateTimeDesc(currentUser.getId());

        return notifications.stream()
                .map(this::mapToNotificationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount() {
        User currentUser = getCurrentUser();
        return notificationRepository.countByRecipientIdAndIsReadFalse(currentUser.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        User currentUser = getCurrentUser();

        Notification notification = notificationRepository.findById(notificationId)
        		.orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NOTIFICATION_NOT_FOUND, null)));

        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED_ACTION, null));
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        User currentUser = getCurrentUser();

        List<Notification> notifications = notificationRepository
                .findByRecipientIdOrderByCreateTimeDesc(currentUser.getId());

        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)));
    }

    private NotificationResponse mapToNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setCreateTime(notification.getCreateTime());
        response.setActorName(notification.getActor().getName());
        response.setActorAvatarUrl(notification.getActor().getAvatarUrl());
        response.setType(notification.getType());
        response.setReferenceId(notification.getReferenceId());
        response.setRead(notification.isRead());
        return response;
    }
}