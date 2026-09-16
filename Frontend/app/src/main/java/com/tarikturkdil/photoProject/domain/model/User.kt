package com.tarikturkdil.photoProject.domain.model

data class User(
    val id: Long,
    val username: String,
    val avatarUrl: String?,
    val bio: String?
)