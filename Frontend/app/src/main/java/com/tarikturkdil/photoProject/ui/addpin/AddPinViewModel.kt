package com.tarikturkdil.photoProject.ui.addpin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.PinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class AddPinFormState(
    val title: String = "",
    val description: String = "",
    val imageUri: Uri? = null
)

sealed interface AddPinStatus {
    data object Idle : AddPinStatus
    data object Loading : AddPinStatus
    data object Success : AddPinStatus
    data class Error(val message: String) : AddPinStatus
}

@HiltViewModel
class AddPinViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pinRepository: PinRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(AddPinFormState())
    val formState: StateFlow<AddPinFormState> = _formState.asStateFlow()

    private val _status = MutableStateFlow<AddPinStatus>(AddPinStatus.Idle)
    val status: StateFlow<AddPinStatus> = _status.asStateFlow()

    fun onTitleChange(value: String) {
        _formState.update { it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _formState.update { it.copy(description = value) }
    }

    fun onImageSelected(uri: Uri) {
        _formState.update { it.copy(imageUri = uri) }
    }

    fun submit() {
        val form = _formState.value

        if (form.title.isBlank()) {
            _status.value = AddPinStatus.Error("Başlık boş bırakılamaz.")
            return
        }
        val uri = form.imageUri
        if (uri == null) {
            _status.value = AddPinStatus.Error("Lütfen bir fotoğraf seçin.")
            return
        }

        viewModelScope.launch {
            _status.value = AddPinStatus.Loading

            val imagePart = try {
                uriToMultipart(uri)
            } catch (e: Exception) {
                _status.value = AddPinStatus.Error("Fotoğraf okunamadı.")
                return@launch
            }

            when (val result = pinRepository.createPin(form.title, form.description.ifBlank { null }, imagePart)) {
                is AppResult.Success -> {
                    _status.value = AddPinStatus.Success
                    _formState.value = AddPinFormState()
                }
                is AppResult.Error -> _status.value = AddPinStatus.Error(result.message)
            }
        }
    }

    fun consumeError() {
        if (_status.value is AddPinStatus.Error) {
            _status.value = AddPinStatus.Idle
        }
    }

    private fun uriToMultipart(uri: Uri): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Dosya açılamadı")

        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        inputStream.close()

        val requestBody = tempFile.asRequestBody("image/*".toMediaType())
        return MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
    }
}