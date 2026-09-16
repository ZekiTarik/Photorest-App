package com.tarikturkdil.photoProject.domain.model

enum class NotificationType {
    NEW_FOLLOWER,
    PIN_LIKED,
    NEW_COMMENT,
    NEW_MESSAGE
}

data class Notification(
    val id: Long,
    val actorName: String?,
    val actorAvatarUrl: String?,
    val type: NotificationType,
    val referenceId: Long?,
    val isRead: Boolean,
    val createTime: String?
)