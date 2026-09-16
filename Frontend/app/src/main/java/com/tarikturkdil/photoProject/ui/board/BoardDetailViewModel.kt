package com.tarikturkdil.photoProject.ui.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BoardDetailUiState {
    data object Loading : BoardDetailUiState
    data class Success(val pins: List<Pin>) : BoardDetailUiState
    data class Error(val message: String) : BoardDetailUiState
}

@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val boardId: Long = checkNotNull(savedStateHandle["boardId"])

    private val _uiState = MutableStateFlow<BoardDetailUiState>(BoardDetailUiState.Loading)
    val uiState: StateFlow<BoardDetailUiState> = _uiState.asStateFlow()

    init {
        loadPins()
    }

    private fun loadPins() {
        viewModelScope.launch {
            _uiState.value = BoardDetailUiState.Loading
            when (val result = boardRepository.getPinsInBoard(boardId)) {
                is AppResult.Success -> _uiState.value = BoardDetailUiState.Success(result.data)
                is AppResult.Error -> _uiState.value = BoardDetailUiState.Error(result.message)
            }
        }
    }

    fun removePin(pinId: Long) {
        viewModelScope.launch {
            when (boardRepository.removePinFromBoard(boardId, pinId)) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        if (current is BoardDetailUiState.Success) {
                            current.copy(pins = current.pins.filterNot { it.id == pinId })
                        } else current
                    }
                }
                is AppResult.Error -> Unit
            }
        }
    }
}