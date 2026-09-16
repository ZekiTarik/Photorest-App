package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.remote.api.UserApi
import com.tarikturkdil.photoProject.data.remote.dto.MyAccountResponse
import com.tarikturkdil.photoProject.domain.model.UserProfile
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.UserRepository
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getMyAccount(): AppResult<UserProfile> {
        return try {
            val response = userApi.getMyAccount()
            val account = response.payload ?: return AppResult.Error("Profil bilgisi alınamadı.")
            AppResult.Success(account.toDomain())
        } catch (e: HttpException) {
            AppResult.Error("Profil yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        }
    }

    override suspend fun updateAvatar(imagePart: MultipartBody.Part): AppResult<UserProfile> {
        return try {
            val response = userApi.updateAvatar(imagePart)
            val account = response.payload ?: return AppResult.Error("Profil fotoğrafı güncellenemedi.")
            AppResult.Success(account.toDomain())
        } catch (e: HttpException) {
            AppResult.Error("Yükleme başarısız (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }
}

private fun MyAccountResponse.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        username = username,
        avatarUrl = avatarUrl,
        bio = bio
    )
}