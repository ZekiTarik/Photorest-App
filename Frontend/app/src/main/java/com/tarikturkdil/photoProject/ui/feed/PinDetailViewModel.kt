package com.tarikturkdil.photoProject.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.model.Board
import com.tarikturkdil.photoProject.domain.model.Comment
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.BoardRepository
import com.tarikturkdil.photoProject.domain.repository.CommentRepository
import com.tarikturkdil.photoProject.domain.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PinDetailUiState {
    data object Loading : PinDetailUiState
    data class Success(val pin: Pin) : PinDetailUiState
    data class Error(val message: String) : PinDetailUiState
}

@HiltViewModel
class PinDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val pinRepository: PinRepository,
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val pinId: Long = checkNotNull(savedStateHandle["pinId"])

    private val _uiState = MutableStateFlow<PinDetailUiState>(PinDetailUiState.Loading)
    val uiState: StateFlow<PinDetailUiState> = _uiState.asStateFlow()

    private val _boards = MutableStateFlow<List<Board>>(emptyList())
    val boards: StateFlow<List<Board>> = _boards.asStateFlow()

    private val _showBoardPicker = MutableStateFlow(false)
    val showBoardPicker: StateFlow<Boolean> = _showBoardPicker.asStateFlow()

    private val _saveStatus = MutableStateFlow<String?>(null)
    val saveStatus: StateFlow<String?> = _saveStatus.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _commentInput = MutableStateFlow("")
    val commentInput: StateFlow<String> = _commentInput.asStateFlow()

    private val _isPostingComment = MutableStateFlow(false)
    val isPostingComment: StateFlow<Boolean> = _isPostingComment.asStateFlow()

    init {
        loadPin()
        loadComments()
    }

    private fun loadPin() {
        viewModelScope.launch {
            _uiState.value = PinDetailUiState.Loading
            when (val result = pinRepository.getPinById(pinId)) {
                is AppResult.Success -> _uiState.value = PinDetailUiState.Success(result.data)
                is AppResult.Error -> _uiState.value = PinDetailUiState.Error(result.message)
            }
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            when (val result = commentRepository.getComments(pinId)) {
                is AppResult.Success -> _comments.value = result.data
                is AppResult.Error -> Unit
            }
        }
    }

    fun toggleLike() {
        val currentState = _uiState.value
        if (currentState !is PinDetailUiState.Success) return

        val pin = currentState.pin
        val wasLiked = pin.isLikedByMe

        _uiState.update {
            if (it is PinDetailUiState.Success) {
                it.copy(pin = it.pin.copy(isLikedByMe = !wasLiked))
            } else it
        }

        viewModelScope.launch {
            val result = if (wasLiked) {
                pinRepository.unlikePin(pinId)
            } else {
                pinRepository.likePin(pinId)
            }

            if (result is AppResult.Error) {
                _uiState.update {
                    if (it is PinDetailUiState.Success) {
                        it.copy(pin = it.pin.copy(isLikedByMe = wasLiked))
                    } else it
                }
            }
        }
    }

    fun openBoardPicker() {
        _showBoardPicker.value = true
        viewModelScope.launch {
            when (val result = boardRepository.getMyBoards()) {
                is AppResult.Success -> _boards.value = result.data
                is AppResult.Error -> _saveStatus.value = result.message
            }
        }
    }

    fun closeBoardPicker() {
        _showBoardPicker.value = false
    }

    fun saveToBoard(boardId: Long) {
        viewModelScope.launch {
            val result = boardRepository.savePinToBoard(boardId, pinId)
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        if (current is PinDetailUiState.Success) {
                            current.copy(pin = current.pin.copy(isSavedByMe = true))
                        } else current
                    }
                    _saveStatus.value = "Panoya kaydedildi."
                }
                is AppResult.Error -> {
                    _saveStatus.value = result.message
                }
            }
            _showBoardPicker.value = false
        }
    }

    fun consumeSaveStatus() {
        _saveStatus.value = null
    }

    fun onCommentInputChange(value: String) {
        _commentInput.value = value
    }

    fun postComment() {
        val text = _commentInput.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            _isPostingComment.value = true
            when (val result = commentRepository.addComment(pinId, text)) {
                is AppResult.Success -> {
                    _comments.update { current -> listOf(result.data) + current }
                    _commentInput.value = ""
                }
                is AppResult.Error -> {
                    _saveStatus.value = result.message
                }
            }
            _isPostingComment.value = false
        }
    }
}