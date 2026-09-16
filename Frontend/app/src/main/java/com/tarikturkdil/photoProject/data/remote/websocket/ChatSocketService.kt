package com.tarikturkdil.photoProject.data.remote.websocket

import com.tarikturkdil.photoProject.data.local.TokenDataStore
import com.tarikturkdil.photoProject.data.remote.dto.MessageResponse
import com.tarikturkdil.photoProject.data.remote.dto.SendMessageRequest
import com.tarikturkdil.photoProject.di.BaseUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatSocketService @Inject constructor(
    private val stompClient: StompClient,
    private val tokenDataStore: TokenDataStore,
    private val json: Json,
    @BaseUrl private val baseUrl: String
) {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var session: org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization? = null
    private var listenJob: Job? = null

    private val _incomingMessages = MutableSharedFlow<MessageResponse>(replay = 0)
    val incomingMessages: SharedFlow<MessageResponse> = _incomingMessages

    private val _connectionState = MutableSharedFlow<Boolean>(replay = 1)
    val connectionState: SharedFlow<Boolean> = _connectionState

    suspend fun connect(): Boolean {
        if (session != null) return true

        val token = tokenDataStore.getAccessToken() ?: return false
        val wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://") + "/ws"

        return try {
            val rawSession = stompClient.connect(
                url = wsUrl,
                customStompConnectHeaders = mapOf("Authorization" to "Bearer $token")
            )
            val jsonSession = rawSession.withJsonConversions(json)
            session = jsonSession

            listenJob = serviceScope.launch {
                jsonSession.subscribe("/user/queue/messages", MessageResponse.serializer())
                    .collect { message -> _incomingMessages.emit(message) }
            }

            true
        } catch (e: Exception) {
            android.util.Log.e("ChatSocketService", "WebSocket bağlantı hatası", e)
            false
        }
    }

    suspend fun sendMessage(conversationId: Long, content: String) {
        val currentSession = session ?: return
        currentSession.convertAndSend(
            "/app/chat.send/$conversationId",
            SendMessageRequest(content),
            SendMessageRequest.serializer()
        )
    }

    suspend fun disconnect() {
        listenJob?.cancel()
        session?.disconnect()
        session = null
        _connectionState.emit(false)
    }
}