package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.remote.api.PinApi
import com.tarikturkdil.photoProject.data.remote.dto.PinResponse
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.PinRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PinRepositoryImpl @Inject constructor(
    private val pinApi: PinApi
) : PinRepository {

    override suspend fun getFeed(): AppResult<List<Pin>> {
        return try {
            val response = pinApi.getFeed()
            val pinResponses = response.payload ?: emptyList()
            AppResult.Success(pinResponses.map { it.toDomain() })
        } catch (e: HttpException) {
            AppResult.Error("Akış yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        }
    }

    override suspend fun getPinById(pinId: Long): AppResult<Pin> {
        return try {
            val response = pinApi.getPinById(pinId)
            val pinResponse = response.payload
                ?: return AppResult.Error("Pin bulunamadı.")
            AppResult.Success(pinResponse.toDomain())
        } catch (e: HttpException) {
            AppResult.Error("Pin yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        }
    }

    override suspend fun likePin(pinId: Long): AppResult<Unit> {
        return try {
            pinApi.likePin(pinId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("Beğeni gönderilemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun unlikePin(pinId: Long): AppResult<Unit> {
        return try {
            pinApi.unlikePin(pinId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("Beğeni geri alınamadı (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun getMyPins(): AppResult<List<Pin>> {
        return try {
            val response = pinApi.getMyPins()
            val pinResponses = response.payload ?: emptyList()
            AppResult.Success(pinResponses.map { it.toDomain() })
        } catch (e: HttpException) {
            AppResult.Error("Pinleriniz yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        }
    }
    override suspend fun deletePin(pinId: Long): AppResult<Unit> {
        return try {
            pinApi.deletePin(pinId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("Pin silinemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun createPin(
        title: String,
        description: String?,
        imagePart: MultipartBody.Part
    ): AppResult<Pin> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaType())
            val descriptionBody = description?.toRequestBody("text/plain".toMediaType())

            val response = pinApi.createPin(titleBody, descriptionBody, imagePart)
            val pin = response.payload?.toDomain()
                ?: return AppResult.Error("Pin oluşturulamadı, sunucudan beklenmeyen yanıt.")
            AppResult.Success(pin)
        } catch (e: HttpException) {
            AppResult.Error("Pin yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        }
    }
}

private fun PinResponse.toDomain(): Pin {
    return Pin(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        ownerUsername = ownerUsername,
        isLikedByMe = likedByMe,
        isSavedByMe = savedByMe
    )
}