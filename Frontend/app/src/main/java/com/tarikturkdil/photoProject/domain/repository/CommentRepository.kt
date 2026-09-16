package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.Comment

interface CommentRepository {
    suspend fun getComments(pinId: Long): AppResult<List<Comment>>
    suspend fun addComment(pinId: Long, text: String): AppResult<Comment>
    suspend fun deleteComment(commentId: Long): AppResult<Unit>
}