package com.herbsapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbsapp.data.repository.AppRepository
import com.herbsapp.data.room.entity.HerbEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlowerInfoViewModel(val repository: AppRepository): ViewModel() {

    private val _herb = MutableStateFlow<HerbEntity?>(null)
    val herb = _herb.asStateFlow()


    fun getHerbById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHerbById(id).collect {
                _herb.value = it
            }
        }
    }


    fun updateHerb(herbEntity: HerbEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (herbEntity.isLiked && !herbEntity.likedAccountsUIDList.contains(repository.currentUser!!.uid)) {
                repository.updateHerb(herbEntity.copy(likedAccountsUIDList = herbEntity.likedAccountsUIDList.plus(repository.currentUser!!.uid)))
            } else {
                repository.updateHerb(herbEntity.copy(likedAccountsUIDList = herbEntity.likedAccountsUIDList.minus(repository.currentUser!!.uid)))
            }
            getHerbById(herbEntity.id)
        }
    }

}