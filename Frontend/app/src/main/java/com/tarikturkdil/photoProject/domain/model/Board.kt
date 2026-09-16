package com.tarikturkdil.photoProject.domain.model

data class Board(
    val id: Long,
    val name: String,
    val description: String?,
    val isSecret: Boolean
)