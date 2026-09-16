package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.remote.api.BoardApi
import com.tarikturkdil.photoProject.data.remote.dto.BoardCreateRequest
import com.tarikturkdil.photoProject.data.remote.dto.BoardResponse
import com.tarikturkdil.photoProject.data.remote.dto.PinResponse
import com.tarikturkdil.photoProject.domain.model.Board
import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.BoardRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val boardApi: BoardApi
) : BoardRepository {

    override suspend fun createBoard(name: String, description: String?, isSecret: Boolean): AppResult<Board> {
        return try {
            val response = boardApi.createBoard(BoardCreateRequest(name, description, isSecret))
            val board = response.payload?.toDomain()
                ?: return AppResult.Error("Pano oluşturulamadı.")
            AppResult.Success(board)
        } catch (e: HttpException) {
            AppResult.Error("Pano oluşturulamadı (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun getMyBoards(): AppResult<List<Board>> {
        return try {
            val response = boardApi.getMyBoards()
            val boards = (response.payload ?: emptyList()).map { it.toDomain() }
            AppResult.Success(boards)
        } catch (e: HttpException) {
            AppResult.Error("Panolar yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun getPinsInBoard(boardId: Long): AppResult<List<Pin>> {
        return try {
            val response = boardApi.getPinsInBoard(boardId)
            val pins = (response.payload ?: emptyList()).map { it.toPinDomain() }
            AppResult.Success(pins)
        } catch (e: HttpException) {
            AppResult.Error("Pinler yüklenemedi (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }

    override suspend fun savePinToBoard(boardId: Long, pinId: Long): AppResult<Unit> {
        return try {
            boardApi.savePinToBoard(boardId, pinId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            if (e.code() == 409) {
                AppResult.Error("Bu pin zaten bu panoya kaydedilmiş.")
            } else {
                AppResult.Error("Kaydedilemedi (kod: ${e.code()}).")
            }
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }
    override suspend fun removePinFromBoard(boardId: Long, pinId: Long): AppResult<Unit> {
        return try {
            boardApi.removePinFromBoard(boardId, pinId)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error("Kaldırılamadı (kod: ${e.code()}).")
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor.")
        }
    }
}

private fun BoardResponse.toDomain(): Board {
    return Board(id = id, name = name, description = description, isSecret = secret)
}

private fun PinResponse.toPinDomain(): Pin {
    return Pin(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        ownerUsername = ownerUsername,
        isLikedByMe = likedByMe
    )
}