package com.tarikturkdil.photoProject.data.remote.api

import com.tarikturkdil.photoProject.data.remote.dto.CommentCreateRequest
import com.tarikturkdil.photoProject.data.remote.dto.CommentResponse
import com.tarikturkdil.photoProject.data.remote.dto.PinResponse
import com.tarikturkdil.photoProject.data.remote.dto.common.RootEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PinApi {

    @GET("/pins/feed")
    suspend fun getFeed(): RootEntity<List<PinResponse>>

    @GET("/pins/{pinId}")
    suspend fun getPinById(@Path("pinId") pinId: Long): RootEntity<PinResponse>

    @GET("/pins")
    suspend fun getMyPins(): RootEntity<List<PinResponse>>

    @GET("/pins/search")
    suspend fun searchPins(@Query("query") query: String): RootEntity<List<PinResponse>>

    @Multipart
    @POST("/pins")
    suspend fun createPin(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody?,
        @Part image: MultipartBody.Part
    ): RootEntity<PinResponse>

    @POST("/pins/{pinId}/like")
    suspend fun likePin(@Path("pinId") pinId: Long): RootEntity<String>

    @DELETE("/pins/{pinId}/like")
    suspend fun unlikePin(@Path("pinId") pinId: Long): RootEntity<String>

    @GET("/pins/{pinId}/comments")
    suspend fun getComments(@Path("pinId") pinId: Long): RootEntity<List<CommentResponse>>

    @POST("/pins/{pinId}/comments")
    suspend fun addComment(
        @Path("pinId") pinId: Long,
        @Body request: CommentCreateRequest
    ): RootEntity<CommentResponse>

    @DELETE("/pins/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Long): RootEntity<String>

    @DELETE("/pins/{pinId}")
    suspend fun deletePin(@Path("pinId") pinId: Long): RootEntity<String>
}