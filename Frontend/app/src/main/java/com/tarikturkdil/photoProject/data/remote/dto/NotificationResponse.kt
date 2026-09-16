package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: Long,
    val actorName: String? = null,
    val actorAvatarUrl: String? = null,
    val type: NotificationType,
    val referenceId: Long? = null,
    val read: Boolean = false,
    val createTime: String? = null
)