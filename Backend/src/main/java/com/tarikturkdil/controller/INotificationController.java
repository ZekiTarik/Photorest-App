package com.tarikturkdil.controller;

import java.util.List;

import com.tarikturkdil.dto.NotificationResponse;

public interface INotificationController {

    public RootEntity<List<NotificationResponse>> getMyNotifications();

    public RootEntity<Long> getUnreadCount();

    public RootEntity<String> markAsRead(Long notificationId);

    public RootEntity<String> markAllAsRead();
}