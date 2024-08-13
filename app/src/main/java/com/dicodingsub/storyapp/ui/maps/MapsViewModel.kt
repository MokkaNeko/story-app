package com.dicodingsub.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicodingsub.storyapp.data.pref.UserModel
import com.dicodingsub.storyapp.data.repository.UserRepository
import com.dicodingsub.storyapp.data.response.StoryListResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val uRepository: UserRepository) : ViewModel() {

    val storiesListResponse: LiveData<StoryListResponse> = uRepository.storyListResponse
    val isLoading: LiveData<Boolean> = uRepository.isLoading

    fun getUser(): LiveData<UserModel> {
        return uRepository.getUser().asLiveData()
    }

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            uRepository.getStoriesWithLocation(token)
        }
    }
}