package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: Long,
    val text: String,
    val username: String? = null,
    val createTime: String? = null
)