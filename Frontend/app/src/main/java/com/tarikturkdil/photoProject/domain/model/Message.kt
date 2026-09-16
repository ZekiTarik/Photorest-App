package com.tarikturkdil.photoProject.domain.model

data class Message(
    val id: Long,
    val senderId: Long,
    val content: String,
    val isRead: Boolean,
    val conversationId: Long,
    val createTime: String?
)