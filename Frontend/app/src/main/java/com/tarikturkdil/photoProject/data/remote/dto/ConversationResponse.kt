package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponse(
    val id: Long,
    val otherUser: UserSummaryResponse,
    val lastMessagePreview: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Long = 0,
    val createTime: String? = null
)