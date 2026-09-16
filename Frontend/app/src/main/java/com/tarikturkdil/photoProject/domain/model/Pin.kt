package com.tarikturkdil.photoProject.domain.model

data class Pin(
    val id: Long,
    val title: String,
    val description: String?,
    val imageUrl: String,
    val ownerUsername: String?,
    val isLikedByMe: Boolean = false,
    val isSavedByMe: Boolean = false
)