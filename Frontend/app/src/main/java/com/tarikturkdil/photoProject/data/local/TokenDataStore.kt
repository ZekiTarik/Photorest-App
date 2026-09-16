package com.tarikturkdil.photoProject.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = androidx.datastore.preferences.core.longPreferencesKey("user_id")
    }

    val accessTokenFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[Keys.ACCESS_TOKEN] }

    val userIdFlow: Flow<Long?> =
        context.dataStore.data.map { prefs -> prefs[Keys.USER_ID] }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
        }
    }

    suspend fun getUserId(): Long? = userIdFlow.first()

    val refreshTokenFlow: Flow<String?> =
        context.dataStore.data.map { prefs -> prefs[Keys.REFRESH_TOKEN] }

    suspend fun getAccessToken(): String? = accessTokenFlow.first()

    suspend fun getRefreshToken(): String? = refreshTokenFlow.first()

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.USER_ID)
        }
    }
}