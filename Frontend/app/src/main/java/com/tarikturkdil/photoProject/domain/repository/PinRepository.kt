package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.Pin
import okhttp3.MultipartBody

interface PinRepository {
    suspend fun getFeed(): AppResult<List<Pin>>
    suspend fun getMyPins(): AppResult<List<Pin>>
    suspend fun getPinById(pinId: Long): AppResult<Pin>
    suspend fun likePin(pinId: Long): AppResult<Unit>
    suspend fun unlikePin(pinId: Long): AppResult<Unit>
    suspend fun createPin(title: String, description: String?, imagePart: MultipartBody.Part): AppResult<Pin>
    suspend fun deletePin(pinId: Long): AppResult<Unit>
}