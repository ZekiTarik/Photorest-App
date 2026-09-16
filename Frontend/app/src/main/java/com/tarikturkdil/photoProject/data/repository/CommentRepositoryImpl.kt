package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.remote.api.PinApi
import com.tarikturkdil.photoProject.data.remote.dto.CommentCreateRequest
import com.tarikturkdil.photoProject.data.remote.dto.CommentResponse
import com.tarikturkdil.photoProject.domain.model.Comment
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.CommentRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val pinApi: PinApi
) : CommentRepository {

    override suspend fun getComments(pinId: Long): AppResult<List<Comment>> {
        return try {
            val response = pinApi.getComments(pinId)
            val comments = (response.payload ?: emptyList()).map { it.toDomain() }
            AppResult.Success(comments)
        } catch (e: HttpException) {
            AppResult.Error("Yorumlar yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun addComment(pinId: Long, text: String): AppResult<Comment> {
        return try {
            val response = pinApi.addComment(pinId, CommentCreateRequest(text))
            val comment = response.payload?.toDomain()
                ?: return AppResult.Error("Yorum eklenemedi.")
            AppResult.Success(comment)
        } catch (e: HttpException) {
            AppResult.Error("Yorum eklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun deleteComment(commentId: Long): AppResult<Unit> {
        return try {
            pinApi.deleteComment(commentId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("Yorum silinemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }
}

private fun CommentResponse.toDomain(): Comment {
    return Comment(id = id, text = text, username = username, createTime = createTime)
}