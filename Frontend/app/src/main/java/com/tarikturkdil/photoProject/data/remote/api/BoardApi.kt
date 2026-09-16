package com.tarikturkdil.photoProject.data.remote.api

import com.tarikturkdil.photoProject.data.remote.dto.BoardCreateRequest
import com.tarikturkdil.photoProject.data.remote.dto.BoardResponse
import com.tarikturkdil.photoProject.data.remote.dto.PinResponse
import com.tarikturkdil.photoProject.data.remote.dto.common.RootEntity
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BoardApi {

    @POST("/boards")
    suspend fun createBoard(@Body request: BoardCreateRequest): RootEntity<BoardResponse>

    @GET("/boards")
    suspend fun getMyBoards(): RootEntity<List<BoardResponse>>

    @GET("/boards/{boardId}/pins")
    suspend fun getPinsInBoard(@Path("boardId") boardId: Long): RootEntity<List<PinResponse>>

    @POST("/boards/{boardId}/pins/{pinId}")
    suspend fun savePinToBoard(
        @Path("boardId") boardId: Long,
        @Path("pinId") pinId: Long
    ): RootEntity<String>

    @DELETE("/boards/{boardId}/pins/{pinId}")
    suspend fun removePinFromBoard(
        @Path("boardId") boardId: Long,
        @Path("pinId") pinId: Long
    ): RootEntity<String>
}