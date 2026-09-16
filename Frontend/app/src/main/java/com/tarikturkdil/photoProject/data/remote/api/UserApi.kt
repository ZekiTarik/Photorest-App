package com.tarikturkdil.photoProject.data.remote.api

import com.tarikturkdil.photoProject.data.remote.dto.MyAccountResponse
import com.tarikturkdil.photoProject.data.remote.dto.UserSummaryResponse
import com.tarikturkdil.photoProject.data.remote.dto.common.RootEntity
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserApi {

    @GET("/users/me")
    suspend fun getMyAccount(): RootEntity<MyAccountResponse>

    @GET("/users/search")
    suspend fun searchUsers(@Query("query") query: String): RootEntity<List<UserSummaryResponse>>

    @Multipart
    @POST("/users/me/avatar")
    suspend fun updateAvatar(@Part image: MultipartBody.Part): RootEntity<MyAccountResponse>
}