package com.tarikturkdil.photoProject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
enum class NotificationType {
    NEW_FOLLOWER,
    PIN_LIKED,
    NEW_COMMENT,
    NEW_MESSAGE
}