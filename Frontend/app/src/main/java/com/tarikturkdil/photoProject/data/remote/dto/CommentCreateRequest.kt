package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentCreateRequest(
    val text: String
)