package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BoardCreateRequest(
    val name: String,
    val description: String? = null,
    val isSecret: Boolean = false
)