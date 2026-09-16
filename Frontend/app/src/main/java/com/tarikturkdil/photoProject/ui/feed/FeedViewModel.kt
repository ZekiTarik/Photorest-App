package com.tarikturkdil.photoProject.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FeedUiState {
    data object Loading : FeedUiState
    data class Success(val pins: List<Pin>) : FeedUiState
    data class Error(val message: String) : FeedUiState
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val pinRepository: PinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadPins()
    }

    fun loadPins() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading
            fetchPins()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchPins()
            _isRefreshing.value = false
        }
    }

    fun toggleLike(pin: Pin) {
        val wasLiked = pin.isLikedByMe
        updatePinInState(pin.id) { it.copy(isLikedByMe = !wasLiked) }

        viewModelScope.launch {
            val result = if (wasLiked) {
                pinRepository.unlikePin(pin.id)
            } else {
                pinRepository.likePin(pin.id)
            }

            if (result is AppResult.Error) {
                updatePinInState(pin.id) { it.copy(isLikedByMe = wasLiked) }
            }
        }
    }

    fun updateLikeStatus(pinId: Long, isLiked: Boolean) {
        updatePinInState(pinId) { it.copy(isLikedByMe = isLiked) }
    }

    private fun updatePinInState(pinId: Long, transform: (Pin) -> Pin) {
        _uiState.update { current ->
            if (current is FeedUiState.Success) {
                val updatedPins = current.pins.map { pin ->
                    if (pin.id == pinId) transform(pin) else pin
                }
                current.copy(pins = updatedPins)
            } else {
                current
            }
        }
    }

    private suspend fun fetchPins() {
        when (val result = pinRepository.getFeed()) {
            is AppResult.Success -> _uiState.value = FeedUiState.Success(result.data)
            is AppResult.Error -> _uiState.value = FeedUiState.Error(result.message)
        }
    }
}