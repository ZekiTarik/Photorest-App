package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.remote.api.ConversationApi
import com.tarikturkdil.photoProject.data.remote.dto.ConversationResponse
import com.tarikturkdil.photoProject.data.remote.dto.MessageResponse
import com.tarikturkdil.photoProject.domain.model.Conversation
import com.tarikturkdil.photoProject.domain.model.Message
import com.tarikturkdil.photoProject.domain.model.UserSummary
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.ConversationRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ConversationRepositoryImpl @Inject constructor(
    private val conversationApi: ConversationApi
) : ConversationRepository {

    override suspend fun startOrGetConversation(targetUserId: Long): AppResult<Conversation> {
        return try {
            val response = conversationApi.startOrGetConversation(targetUserId)
            val conversation = response.payload?.toDomain()
                ?: return AppResult.Error("Konuşma başlatılamadı.")
            AppResult.Success(conversation)
        } catch (e: HttpException) {
            AppResult.Error("Konuşma başlatılamadı (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun getMyConversations(): AppResult<List<Conversation>> {
        return try {
            val response = conversationApi.getMyConversations()
            val conversations = (response.payload ?: emptyList()).map { it.toDomain() }
            AppResult.Success(conversations)
        } catch (e: HttpException) {
            AppResult.Error("Konuşmalar yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun getMessages(conversationId: Long): AppResult<List<Message>> {
        return try {
            val response = conversationApi.getMessages(conversationId)
            val messages = (response.payload ?: emptyList()).map { it.toDomain() }
            AppResult.Success(messages)
        } catch (e: HttpException) {
            AppResult.Error("Mesajlar yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun markMessagesAsRead(conversationId: Long): AppResult<Unit> {
        return try {
            conversationApi.markMessagesAsRead(conversationId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("İşaretlenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }
}

private fun ConversationResponse.toDomain(): Conversation {
    return Conversation(
        id = id,
        otherUser = UserSummary(id = otherUser.id, name = otherUser.name, avatarUrl = otherUser.avatarUrl),
        lastMessagePreview = lastMessagePreview,
        unreadCount = unreadCount
    )
}

private fun MessageResponse.toDomain(): Message {
    return Message(
        id = id,
        senderId = senderId,
        content = content,
        isRead = read,
        conversationId = conversationId,
        createTime = createTime
    )
}