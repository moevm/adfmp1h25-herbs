package com.herbsapp.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.currentComposer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.herbsapp.R
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

    private val _changeMailState = MutableStateFlow<Resource<String>?>(null)
    val changeMailState: StateFlow<Resource<String>?> = _changeMailState

    val currentUser: FirebaseUser?
        get() = repository.currentUser

//    val name = MutableStateFlow(currentUser?.displayName)

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

    fun loginByGuest(context: Context) = viewModelScope.launch {
        _login.value = Resource.Loading
        val result = repository.login(context.getString(R.string.guest_login), context.getString(R.string.guest_password))
        _login.value = result
    }

    fun changeName(name: String) = viewModelScope.launch {
        repository.changeName(name)
    }

    fun changeEmail(mail: String) = viewModelScope.launch {
        _changeMailState.value = Resource.Loading
        val result = repository.changeMail(mail)
        _changeMailState.value = result
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

    fun isGuest(context: Context) = currentUser!!.uid == context.getString(R.string.guest_uid)

}