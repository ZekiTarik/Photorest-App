package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.Pin
import com.tarikturkdil.photoProject.domain.model.UserSummary

interface SearchRepository {
    suspend fun searchPins(query: String): AppResult<List<Pin>>
    suspend fun searchUsers(query: String): AppResult<List<UserSummary>>
}