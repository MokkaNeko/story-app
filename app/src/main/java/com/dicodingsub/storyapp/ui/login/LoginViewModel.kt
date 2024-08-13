package com.dicodingsub.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicodingsub.storyapp.data.pref.UserModel
import com.dicodingsub.storyapp.data.repository.UserRepository
import com.dicodingsub.storyapp.data.response.LoginResponse
import com.dicodingsub.storyapp.di.Event
import kotlinx.coroutines.launch

class LoginViewModel(private val uRepository: UserRepository) : ViewModel() {
    val loginResponse: LiveData<LoginResponse> = uRepository.loginResponse

    val isLoading: LiveData<Boolean> = uRepository.isLoading

    val toastText: LiveData<Event<String>> = uRepository.toastText


    fun login(email: String, password: String) {
        viewModelScope.launch {
            uRepository.login(email, password)
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            uRepository.saveSession(user)
        }
    }


}