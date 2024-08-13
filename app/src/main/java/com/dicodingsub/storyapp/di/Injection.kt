package com.dicodingsub.storyapp.di

import android.content.Context
import com.dicodingsub.storyapp.data.api.ApiConfig
import com.dicodingsub.storyapp.data.pref.UserPreference
import com.dicodingsub.storyapp.data.pref.dataStore
import com.dicodingsub.storyapp.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(userPreference, apiService)
    }
}