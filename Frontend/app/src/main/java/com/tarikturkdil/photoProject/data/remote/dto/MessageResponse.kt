package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val id: Long,
    val senderId: Long,
    val content: String,
    val read: Boolean = false,
    val conversationId: Long,
    val createTime: String? = null
)