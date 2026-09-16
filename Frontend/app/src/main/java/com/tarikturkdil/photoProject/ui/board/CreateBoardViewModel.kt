package com.tarikturkdil.photoProject.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.BoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateBoardFormState(
    val name: String = "",
    val description: String = "",
    val isSecret: Boolean = false
)

sealed interface CreateBoardStatus {
    data object Idle : CreateBoardStatus
    data object Loading : CreateBoardStatus
    data object Success : CreateBoardStatus
    data class Error(val message: String) : CreateBoardStatus
}

@HiltViewModel
class CreateBoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(CreateBoardFormState())
    val formState: StateFlow<CreateBoardFormState> = _formState.asStateFlow()

    private val _status = MutableStateFlow<CreateBoardStatus>(CreateBoardStatus.Idle)
    val status: StateFlow<CreateBoardStatus> = _status.asStateFlow()

    fun onNameChange(value: String) = _formState.update { it.copy(name = value) }
    fun onDescriptionChange(value: String) = _formState.update { it.copy(description = value) }
    fun onSecretChange(value: Boolean) = _formState.update { it.copy(isSecret = value) }

    fun submit() {
        val form = _formState.value
        if (form.name.isBlank()) {
            _status.value = CreateBoardStatus.Error("Pano adı boş bırakılamaz.")
            return
        }

        viewModelScope.launch {
            _status.value = CreateBoardStatus.Loading
            when (val result = boardRepository.createBoard(form.name, form.description.ifBlank { null }, form.isSecret)) {
                is AppResult.Success -> _status.value = CreateBoardStatus.Success
                is AppResult.Error -> _status.value = CreateBoardStatus.Error(result.message)
            }
        }
    }

    fun consumeError() {
        if (_status.value is CreateBoardStatus.Error) _status.value = CreateBoardStatus.Idle
    }
}