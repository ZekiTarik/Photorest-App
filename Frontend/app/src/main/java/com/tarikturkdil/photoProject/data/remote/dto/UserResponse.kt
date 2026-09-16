package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long? = null,
    val username: String,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val createTime: String? = null
)