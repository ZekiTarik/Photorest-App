package com.tarikturkdil.service;

import java.util.List;

import com.tarikturkdil.dto.NotificationResponse;

public interface INotificationService {

	List<NotificationResponse> getMyNotifications();
	
	long getUnreadCount();
	
	void markAsRead(Long notificationId);
	
	void markAllAsRead();
}
