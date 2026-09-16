package com.tarikturkdil.photoProject.di

import com.tarikturkdil.photoProject.data.repository.AuthRepositoryImpl
import com.tarikturkdil.photoProject.data.repository.PinRepositoryImpl
import com.tarikturkdil.photoProject.data.repository.UserRepositoryImpl
import com.tarikturkdil.photoProject.domain.repository.AuthRepository
import com.tarikturkdil.photoProject.domain.repository.PinRepository
import com.tarikturkdil.photoProject.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.tarikturkdil.photoProject.data.repository.BoardRepositoryImpl
import com.tarikturkdil.photoProject.data.repository.CommentRepositoryImpl
import com.tarikturkdil.photoProject.data.repository.ConversationRepositoryImpl
import com.tarikturkdil.photoProject.data.repository.NotificationRepositoryImpl
import com.tarikturkdil.photoProject.data.repository.SearchRepositoryImpl
import com.tarikturkdil.photoProject.domain.repository.BoardRepository
import com.tarikturkdil.photoProject.domain.repository.CommentRepository
import com.tarikturkdil.photoProject.domain.repository.ConversationRepository
import com.tarikturkdil.photoProject.domain.repository.NotificationRepository
import com.tarikturkdil.photoProject.domain.repository.SearchRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository


    @Binds
    @Singleton
    abstract fun bindBoardRepository(impl: BoardRepositoryImpl): BoardRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindPinRepository(impl: PinRepositoryImpl): PinRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(impl: CommentRepositoryImpl): CommentRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository
}