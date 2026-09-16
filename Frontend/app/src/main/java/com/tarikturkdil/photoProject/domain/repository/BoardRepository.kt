package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.Board
import com.tarikturkdil.photoProject.domain.model.Pin

interface BoardRepository {
    suspend fun createBoard(name: String, description: String?, isSecret: Boolean): AppResult<Board>
    suspend fun getMyBoards(): AppResult<List<Board>>
    suspend fun getPinsInBoard(boardId: Long): AppResult<List<Pin>>
    suspend fun savePinToBoard(boardId: Long, pinId: Long): AppResult<Unit>
    suspend fun removePinFromBoard(boardId: Long, pinId: Long): AppResult<Unit>
}