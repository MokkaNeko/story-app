package com.dicodingsub.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicodingsub.storyapp.data.repository.UserRepository
import com.dicodingsub.storyapp.data.response.RegisterResponse
import com.dicodingsub.storyapp.di.Event
import kotlinx.coroutines.launch

class RegisterViewModel(private val uRepository: UserRepository) : ViewModel() {
    val registerResponse: LiveData<RegisterResponse> = uRepository.registerResponse
    val isLoading: LiveData<Boolean> = uRepository.isLoading
    val toastText: LiveData<Event<String>> = uRepository.toastText

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            uRepository.register(name, email, password)
        }
    }
}