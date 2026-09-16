package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.remote.api.PinApi
import com.tarikturkdil.photoProject.data.remote.api.UserApi
import com.tarikturkdil.photoProject.data.remote.dto.PinResponse
import com.tarikturkdil.photoProject.data.remote.dto.UserSummaryResponse
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.model.UserSummary
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.SearchRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val pinApi: PinApi,
    private val userApi: UserApi
) : SearchRepository {

    override suspend fun searchPins(query: String): AppResult<List<Pin>> {
        return try {
            val response = pinApi.searchPins(query)
            val pins = (response.payload ?: emptyList()).map { it.toPinDomain() }
            AppResult.Success(pins)
        } catch (e: HttpException) {
            AppResult.Error("Arama başarısız (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun searchUsers(query: String): AppResult<List<UserSummary>> {
        return try {
            val response = userApi.searchUsers(query)
            val users = (response.payload ?: emptyList()).map { it.toDomain() }
            AppResult.Success(users)
        } catch (e: HttpException) {
            AppResult.Error("Arama başarısız (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }
}

private fun PinResponse.toPinDomain(): Pin {
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

private fun UserSummaryResponse.toDomain(): UserSummary {
    return UserSummary(id = id, name = name, avatarUrl = avatarUrl)
}