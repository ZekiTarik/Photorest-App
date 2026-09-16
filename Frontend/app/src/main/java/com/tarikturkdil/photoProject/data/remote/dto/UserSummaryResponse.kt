package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSummaryResponse(
    val id: Long,
    val name: String,
    val avatarUrl: String? = null
)