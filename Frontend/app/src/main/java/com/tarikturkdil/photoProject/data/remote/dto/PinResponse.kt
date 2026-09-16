package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PinResponse(
    val id: Long,
    val title: String,
    val description: String? = null,
    val imageUrl: String,
    val ownerUsername: String? = null,
    val likedByMe: Boolean = false,
    val savedByMe: Boolean = false,
    val createTime: String? = null
)