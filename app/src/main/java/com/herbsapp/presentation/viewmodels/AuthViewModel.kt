package com.herbsapp.presentation.viewmodels

import android.util.Log
import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.herbsapp.data.repository.AppRepository
import com.herbsapp.presentation.ui.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AppRepository): ViewModel() {

    private val _login = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val login: StateFlow<Resource<FirebaseUser>?> = _login

    private val _register = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val register: StateFlow<Resource<FirebaseUser>?> = _register

    val currentUser: FirebaseUser?
        get() = repository.currentUser

    val name = MutableStateFlow(currentUser?.displayName)

    init {
        if (repository.currentUser != null) {
            _login.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        _login.value = Resource.Loading
        val result = repository.login(email, password)
        _login.value = result
    }

    fun changeName(name: String) = viewModelScope.launch {
        repository.changeName(name)
    }

    fun changeEmail(mail: String) = viewModelScope.launch {
        repository.changeMail(mail)
    }

    fun registerUser(name: String, email: String, password: String) = viewModelScope.launch {
        _register.value = Resource.Loading
        val result = repository.signup(name, email, password)
        _register.value = result
    }

    fun logout() {
        repository.logout()
        _login.value = null
        _register.value = null
    }
}