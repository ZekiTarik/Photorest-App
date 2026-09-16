package com.tarikturkdil.photoProject.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import javax.inject.Singleton
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    @Singleton
    fun provideStompClient(): StompClient {
        return StompClient(OkHttpWebSocketClient())
    }

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String {
        return com.tarikturkdil.photoProject.BuildConfig.BASE_URL
    }
}