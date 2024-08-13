package com.dicodingsub.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicodingsub.storyapp.data.pref.UserModel
import com.dicodingsub.storyapp.data.repository.UserRepository
import com.dicodingsub.storyapp.data.response.LoginResponse
import com.dicodingsub.storyapp.data.response.StoryListResponse
import com.dicodingsub.storyapp.data.response.StoryResponse
import kotlinx.coroutines.launch

class MainViewModel(private val uRepository: UserRepository) : ViewModel() {

    val storiesListResponse: LiveData<StoryListResponse> = uRepository.storyListResponse
    val isLoading: LiveData<Boolean> = uRepository.isLoading
    val storiesListPaging: LiveData<PagingData<StoryResponse>> =
        uRepository.getStories().cachedIn(viewModelScope)

    fun getUser(): LiveData<UserModel> {
        return uRepository.getUser().asLiveData()
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            uRepository.saveSession(user)
        }
    }

    fun logout() {
        viewModelScope.launch {
            uRepository.logout()
        }
    }
}