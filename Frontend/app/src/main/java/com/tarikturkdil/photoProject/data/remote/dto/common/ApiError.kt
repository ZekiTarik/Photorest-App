package com.tarikturkdil.photoProject.data.remote.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val status: Int? = null,
    val exception: ExceptionDetail? = null
)

@Serializable
data class ExceptionDetail(
    val path: String? = null,
    val createTime: String? = null,
    val hostName: String? = null,
    val message: String? = null
)