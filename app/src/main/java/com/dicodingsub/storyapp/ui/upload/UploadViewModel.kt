package com.dicodingsub.storyapp.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicodingsub.storyapp.data.pref.UserModel
import com.dicodingsub.storyapp.data.repository.UserRepository
import com.dicodingsub.storyapp.data.response.UploadResponse
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel(private val uRepository: UserRepository) : ViewModel() {
//    val isLoading: LiveData<Boolean> = uRepository.isLoading
    val uploadResponse: LiveData<UploadResponse> = uRepository.uploadStoryResponse

    fun uploadStory(token: String, imageFile: File, description: String) {
        viewModelScope.launch {
            uRepository.uploadStory(token, imageFile, description)
        }
    }

    fun getUser(): LiveData<UserModel> {
        return uRepository.getUser().asLiveData()
    }
}
