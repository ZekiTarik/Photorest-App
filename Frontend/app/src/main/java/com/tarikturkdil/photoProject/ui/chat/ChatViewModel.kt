package com.tarikturkdil.photoProject.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.data.remote.websocket.ChatSocketService
import com.tarikturkdil.photoProject.domain.model.Message
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val messages: List<Message>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val conversationRepository: ConversationRepository,
    private val chatSocketService: ChatSocketService,
    private val userRepository: com.tarikturkdil.photoProject.domain.repository.UserRepository
) : ViewModel() {

    private val conversationId: Long = checkNotNull(savedStateHandle["conversationId"])

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError.asStateFlow()

    init {
        viewModelScope.launch {
            val result = userRepository.getMyAccount()
            if (result is com.tarikturkdil.photoProject.domain.repository.AppResult.Success) {
                _currentUserId.value = result.data.id
            }
        }
        loadHistory()
        connectAndListen()
        markAsRead()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Loading
            when (val result = conversationRepository.getMessages(conversationId)) {
                is com.tarikturkdil.photoProject.domain.repository.AppResult.Success -> _uiState.value = ChatUiState.Success(result.data)
                is com.tarikturkdil.photoProject.domain.repository.AppResult.Error -> _uiState.value = ChatUiState.Error(result.message)
            }
        }
    }

    private fun markAsRead() {
        viewModelScope.launch {
            conversationRepository.markMessagesAsRead(conversationId)
        }
    }

    private fun connectAndListen() {
        viewModelScope.launch {
            val connected = chatSocketService.connect()
            if (!connected) {
                _connectionError.value = "Canlı bağlantı kurulamadı. Mesajlar gönderilemeyebilir."
                return@launch
            }

            chatSocketService.incomingMessages.collect { incoming ->
                if (incoming.conversationId != conversationId) return@collect

                val newMessage = Message(
                    id = incoming.id,
                    senderId = incoming.senderId,
                    content = incoming.content,
                    isRead = incoming.read,
                    conversationId = incoming.conversationId,
                    createTime = incoming.createTime
                )

                _uiState.update { current ->
                    if (current is ChatUiState.Success) {
                        current.copy(messages = current.messages + newMessage)
                    } else {
                        ChatUiState.Success(listOf(newMessage))
                    }
                }
            }
        }
    }

    fun onMessageInputChange(value: String) {
        _messageInput.value = value
    }

    fun sendMessage() {
        val content = _messageInput.value.trim()
        if (content.isBlank()) return

        viewModelScope.launch {
            chatSocketService.sendMessage(conversationId, content)
            _messageInput.value = ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            chatSocketService.disconnect()
        }
    }
}