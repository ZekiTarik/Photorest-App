package com.tarikturkdil.photoProject.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.model.Board
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.model.UserProfile
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.BoardRepository
import com.tarikturkdil.photoProject.domain.repository.PinRepository
import com.tarikturkdil.photoProject.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val profile: UserProfile, val pins: List<Pin>, val boards: List<Board>) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val pinRepository: PinRepository,
    private val boardRepository: BoardRepository,
    private val tokenDataStore: com.tarikturkdil.photoProject.data.local.TokenDataStore,
    private val authRepository: com.tarikturkdil.photoProject.domain.repository.AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isUploadingAvatar = MutableStateFlow(false)
    val isUploadingAvatar: StateFlow<Boolean> = _isUploadingAvatar.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            loadProfileInternal()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadProfileInternal()
            _isRefreshing.value = false
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loggedOut.value = true
        }
    }

    private suspend fun loadProfileInternal() {
        val profileResult = userRepository.getMyAccount()
        val pinsResult = pinRepository.getMyPins()
        val boardsResult = boardRepository.getMyBoards()

        if (profileResult is AppResult.Success && pinsResult is AppResult.Success && boardsResult is AppResult.Success) {
            tokenDataStore.saveUserId(profileResult.data.id) // YENİ SATIR
            _uiState.value = ProfileUiState.Success(profileResult.data, pinsResult.data, boardsResult.data)
        } else {
            val errorMessage = (profileResult as? AppResult.Error)?.message
                ?: (pinsResult as? AppResult.Error)?.message
                ?: (boardsResult as? AppResult.Error)?.message
                ?: "Profil yüklenemedi."
            _uiState.value = ProfileUiState.Error(errorMessage)
        }
    }

    fun updateAvatar(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            _isUploadingAvatar.value = true
            when (val result = userRepository.updateAvatar(imagePart)) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        if (current is ProfileUiState.Success) current.copy(profile = result.data) else current
                    }
                }
                is AppResult.Error -> Unit
            }
            _isUploadingAvatar.value = false
        }
    }

    fun deletePin(pin: Pin, onDeleted: () -> Unit) {
        viewModelScope.launch {
            when (pinRepository.deletePin(pin.id)) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        if (current is ProfileUiState.Success) {
                            current.copy(pins = current.pins.filterNot { it.id == pin.id })
                        } else current
                    }
                    onDeleted()
                }
                is AppResult.Error -> Unit
            }
        }
    }




}