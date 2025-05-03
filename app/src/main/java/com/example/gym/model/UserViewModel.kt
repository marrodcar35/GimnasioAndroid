// UserViewModel.kt
package com.example.gym.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    val userName = MutableLiveData<String>()
    val peso = MutableLiveData<String>()
    val altura = MutableLiveData<String>()
}
