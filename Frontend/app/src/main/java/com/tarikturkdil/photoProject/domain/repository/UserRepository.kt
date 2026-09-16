package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.UserProfile
import okhttp3.MultipartBody

interface UserRepository {
    suspend fun getMyAccount(): AppResult<UserProfile>
    suspend fun updateAvatar(imagePart: MultipartBody.Part): AppResult<UserProfile>
}