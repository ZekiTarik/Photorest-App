package com.tarikturkdil.photoProject.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.model.Conversation
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ConversationsUiState {
    data object Loading : ConversationsUiState
    data class Success(val conversations: List<Conversation>) : ConversationsUiState
    data class Error(val message: String) : ConversationsUiState
}

@HiltViewModel
class ConversationsListViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConversationsUiState>(ConversationsUiState.Loading)
    val uiState: StateFlow<ConversationsUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _uiState.value = ConversationsUiState.Loading
            when (val result = conversationRepository.getMyConversations()) {
                is AppResult.Success -> _uiState.value = ConversationsUiState.Success(result.data)
                is AppResult.Error -> _uiState.value = ConversationsUiState.Error(result.message)
            }
        }
    }
}