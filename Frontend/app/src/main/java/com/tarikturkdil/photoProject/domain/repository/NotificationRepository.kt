package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.Notification

interface NotificationRepository {
    suspend fun getMyNotifications(): AppResult<List<Notification>>
    suspend fun getUnreadCount(): AppResult<Long>
    suspend fun markAsRead(notificationId: Long): AppResult<Unit>
    suspend fun markAllAsRead(): AppResult<Unit>
}