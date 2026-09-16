package com.tarikturkdil.photoProject.di

import com.tarikturkdil.photoProject.BuildConfig
import com.tarikturkdil.photoProject.data.remote.AuthInterceptor
import com.tarikturkdil.photoProject.data.remote.TokenAuthenticator
import com.tarikturkdil.photoProject.data.remote.api.AuthApi
import com.tarikturkdil.photoProject.data.remote.api.BoardApi
import com.tarikturkdil.photoProject.data.remote.api.ConversationApi
import com.tarikturkdil.photoProject.data.remote.api.NotificationApi
import com.tarikturkdil.photoProject.data.remote.api.PinApi
import com.tarikturkdil.photoProject.data.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

private val BASE_URL = BuildConfig.BASE_URL

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun providePinApi(retrofit: Retrofit): PinApi {
        return retrofit.create(PinApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBoardApi(retrofit: Retrofit): BoardApi {
        return retrofit.create(BoardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideConversationApi(retrofit: Retrofit): ConversationApi {
        return retrofit.create(ConversationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCoilImageLoader(
        @ApplicationContext context: android.content.Context,
        okHttpClient: OkHttpClient
    ): coil.ImageLoader {
        return coil.ImageLoader.Builder(context)
            .okHttpClient(okHttpClient)
            .crossfade(true)
            .memoryCache {
                coil.memory.MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                coil.disk.DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            .build()
    }
}