package com.tarikturkdil.photoProject.domain.model

data class UserProfile(
    val id: Long,
    val username: String,
    val avatarUrl: String?,
    val bio: String?
)