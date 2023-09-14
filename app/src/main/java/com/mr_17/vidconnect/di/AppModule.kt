package com.mr_17.vidconnect.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.mr_17.vidconnect.ui.auth.AuthRepository
import com.mr_17.vidconnect.ui.auth.AuthRepositoryImpl
import com.mr_17.vidconnect.ui.home.HomeRepository
import com.mr_17.vidconnect.ui.home.HomeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context) : Context = context.applicationContext

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun providesHomeRepository(impl: HomeRepositoryImpl): HomeRepository = impl
}