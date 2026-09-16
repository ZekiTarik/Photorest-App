package com.tarikturkdil.photoProject.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.model.UserSummary
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val pins: List<Pin>, val users: List<UserSummary>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val conversationRepository: com.tarikturkdil.photoProject.domain.repository.ConversationRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _navigateToChat = MutableStateFlow<Long?>(null)
    val navigateToChat: StateFlow<Long?> = _navigateToChat.asStateFlow()

    private val _startChatError = MutableStateFlow<String?>(null)
    val startChatError: StateFlow<String?> = _startChatError.asStateFlow()


    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        searchJob = viewModelScope.launch {
            delay(400) // debounce: kullanıcı yazmayı bırakana kadar bekle
            performSearch(newQuery)
        }
    }

    fun startChatWith(targetUserId: Long) {
        viewModelScope.launch {
            when (val result = conversationRepository.startOrGetConversation(targetUserId)) {
                is AppResult.Success -> _navigateToChat.value = result.data.id
                is AppResult.Error -> _startChatError.value = result.message
            }
        }
    }

    fun consumeNavigation() {
        _navigateToChat.value = null
    }

    fun consumeStartChatError() {
        _startChatError.value = null
    }

    private suspend fun performSearch(searchQuery: String) {
        _uiState.value = SearchUiState.Loading

        val pinsResult = searchRepository.searchPins(searchQuery)
        val usersResult = searchRepository.searchUsers(searchQuery)

        if (pinsResult is AppResult.Success && usersResult is AppResult.Success) {
            _uiState.value = SearchUiState.Success(pinsResult.data, usersResult.data)
        } else {
            val errorMessage = (pinsResult as? AppResult.Error)?.message
                ?: (usersResult as? AppResult.Error)?.message
                ?: "Arama başarısız."
            _uiState.value = SearchUiState.Error(errorMessage)
        }
    }
}