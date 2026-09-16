package com.tarikturkdil.photoProject.domain.model

data class Conversation(
    val id: Long,
    val otherUser: UserSummary,
    val lastMessagePreview: String?,
    val unreadCount: Long
)