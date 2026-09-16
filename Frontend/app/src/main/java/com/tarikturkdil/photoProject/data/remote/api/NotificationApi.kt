package com.tarikturkdil.photoProject.data.remote.api

import com.tarikturkdil.photoProject.data.remote.dto.NotificationResponse
import com.tarikturkdil.photoProject.data.remote.dto.common.RootEntity
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationApi {

    @GET("/notifications")
    suspend fun getMyNotifications(): RootEntity<List<NotificationResponse>>

    @GET("/notifications/unread-count")
    suspend fun getUnreadCount(): RootEntity<Long>

    @POST("/notifications/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId: Long): RootEntity<String>

    @POST("/notifications/read-all")
    suspend fun markAllAsRead(): RootEntity<String>
}