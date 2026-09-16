package com.tarikturkdil.photoProject.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.model.Notification
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NotificationsUiState {
    data object Loading : NotificationsUiState
    data class Success(val notifications: List<Notification>) : NotificationsUiState
    data class Error(val message: String) : NotificationsUiState
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationsUiState.Loading
            when (val result = notificationRepository.getMyNotifications()) {
                is AppResult.Success -> _uiState.value = NotificationsUiState.Success(result.data)
                is AppResult.Error -> _uiState.value = NotificationsUiState.Error(result.message)
            }
        }
    }

    fun onNotificationClicked(notification: Notification) {
        if (notification.isRead) return

        viewModelScope.launch {
            val result = notificationRepository.markAsRead(notification.id)
            if (result is AppResult.Success) {
                _uiState.update { current ->
                    if (current is NotificationsUiState.Success) {
                        val updated = current.notifications.map {
                            if (it.id == notification.id) it.copy(isRead = true) else it
                        }
                        current.copy(notifications = updated)
                    } else current
                }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val result = notificationRepository.markAllAsRead()
            if (result is AppResult.Success) {
                _uiState.update { current ->
                    if (current is NotificationsUiState.Success) {
                        current.copy(notifications = current.notifications.map { it.copy(isRead = true) })
                    } else current
                }
            }
        }
    }
}