package com.tarikturkdil.photoProject.data.remote.api

import com.tarikturkdil.photoProject.data.remote.dto.ConversationResponse
import com.tarikturkdil.photoProject.data.remote.dto.MessageResponse
import com.tarikturkdil.photoProject.data.remote.dto.common.RootEntity
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ConversationApi {

    @POST("/conversations/{targetUserId}")
    suspend fun startOrGetConversation(@Path("targetUserId") targetUserId: Long): RootEntity<ConversationResponse>

    @GET("/conversations")
    suspend fun getMyConversations(): RootEntity<List<ConversationResponse>>

    @GET("/conversations/{conversationId}/messages")
    suspend fun getMessages(@Path("conversationId") conversationId: Long): RootEntity<List<MessageResponse>>

    @POST("/conversations/{conversationId}/read")
    suspend fun markMessagesAsRead(@Path("conversationId") conversationId: Long): RootEntity<String>
}
