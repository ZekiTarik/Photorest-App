package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.remote.api.NotificationApi
import com.tarikturkdil.photoProject.data.remote.dto.NotificationResponse
import com.tarikturkdil.photoProject.domain.model.Notification
import com.tarikturkdil.photoProject.domain.model.NotificationType
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.NotificationRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationApi: NotificationApi
) : NotificationRepository {

    override suspend fun getMyNotifications(): AppResult<List<Notification>> {
        return try {
            val response = notificationApi.getMyNotifications()
            val notifications = (response.payload ?: emptyList()).map { it.toDomain() }
            AppResult.Success(notifications)
        } catch (e: HttpException) {
            AppResult.Error("Bildirimler yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun getUnreadCount(): AppResult<Long> {
        return try {
            val response = notificationApi.getUnreadCount()
            AppResult.Success(response.payload ?: 0L)
        } catch (e: HttpException) {
            AppResult.Error("Sayı alınamadı (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun markAsRead(notificationId: Long): AppResult<Unit> {
        return try {
            notificationApi.markAsRead(notificationId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("İşaretlenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun markAllAsRead(): AppResult<Unit> {
        return try {
            notificationApi.markAllAsRead()
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("İşaretlenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }
}

private fun NotificationResponse.toDomain(): Notification {
    return Notification(
        id = id,
        actorName = actorName,
        actorAvatarUrl = actorAvatarUrl,
        type = when (type) {
            com.tarikturkdil.photoProject.data.remote.dto.NotificationType.NEW_FOLLOWER -> NotificationType.NEW_FOLLOWER
            com.tarikturkdil.photoProject.data.remote.dto.NotificationType.PIN_LIKED -> NotificationType.PIN_LIKED
            com.tarikturkdil.photoProject.data.remote.dto.NotificationType.NEW_COMMENT -> NotificationType.NEW_COMMENT
            com.tarikturkdil.photoProject.data.remote.dto.NotificationType.NEW_MESSAGE -> NotificationType.NEW_MESSAGE
        },
        referenceId = referenceId,
        isRead = read,  // DTO'daki "read" alanını, domain modeldeki "isRead" alanına atıyoruz
        createTime = createTime
    )
}