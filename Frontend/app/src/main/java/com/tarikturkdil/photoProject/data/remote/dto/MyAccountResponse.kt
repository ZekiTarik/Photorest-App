package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MyAccountResponse(
    val id: Long,
    val username: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val createTime: String? = null
)