package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.Conversation
import com.tarikturkdil.photoProject.domain.model.Message

interface ConversationRepository {
    suspend fun startOrGetConversation(targetUserId: Long): AppResult<Conversation>
    suspend fun getMyConversations(): AppResult<List<Conversation>>
    suspend fun getMessages(conversationId: Long): AppResult<List<Message>>
    suspend fun markMessagesAsRead(conversationId: Long): AppResult<Unit>
}