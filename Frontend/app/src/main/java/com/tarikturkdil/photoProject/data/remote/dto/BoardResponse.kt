package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class BoardResponse(
    val id: Long,
    val name: String,
    val description: String? = null,
    val secret: Boolean = false,
    val createTime: String? = null
)