package com.tarikturkdil.photoProject.data.remote.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class RootEntity<T>(
    val payload: T? = null,
    val status: Int? = null
)