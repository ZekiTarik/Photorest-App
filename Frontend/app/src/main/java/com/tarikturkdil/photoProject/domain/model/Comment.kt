package com.tarikturkdil.photoProject.domain.model

data class Comment(
    val id: Long,
    val text: String,
    val username: String?,
    val createTime: String?
)