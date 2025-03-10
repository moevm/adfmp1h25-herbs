package com.herbsapp.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbsapp.R
import com.herbsapp.data.repository.AppRepository
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.Determiner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeterminerViewModel(repository: AppRepository, context: Context) : ViewModel() {

    val _allHerbs = MutableStateFlow<List<HerbEntity>>(mutableStateListOf())

    val resultHerbs: MutableStateFlow<MutableList<HerbEntity>> = MutableStateFlow(mutableListOf())
    val currentHerbID = MutableStateFlow<Int>(0)
    val determinerList = MutableStateFlow(
        mutableStateListOf(
            Determiner(
                context.getString(R.string.determiner_question_1),
                listOf(
                    context.getString(R.string.determiner_question_1_var1),
                    context.getString(R.string.determiner_question_1_var2),
                    context.getString(R.string.determiner_question_1_var3),
                ),
                null
            ),
            Determiner(
                context.getString(R.string.determiner_question_2),
                listOf(
                    context.getString(R.string.determiner_question_2_var1),
                    context.getString(R.string.determiner_question_2_var2),
                    context.getString(R.string.determiner_question_2_var3),
                ),
                null
            ),
            Determiner(
                context.getString(R.string.determiner_question_3),
                listOf(
                    context.getString(R.string.determiner_question_3_var1),
                    context.getString(R.string.determiner_question_3_var2),
                    context.getString(R.string.determiner_question_3_var3),
                ),
                null
            ),
            Determiner(
                context.getString(R.string.determiner_question_4),
                listOf(
                    context.getString(R.string.determiner_question_4_var1),
                    context.getString(R.string.determiner_question_4_var2),
                ),
                null
            ),
        )
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHerbs().collect {
                _allHerbs.value = it
            }
        }
    }

    fun nextHerb(onEndList: () -> Unit) {
        if (resultHerbs.value.size > currentHerbID.value + 1) {
            currentHerbID.value++
        } else {
            determinerList.update {
                it.apply { replaceAll { it.copy(selectVariant = null) } }
            }
            onEndList()
        }
    }


    fun choose(determiner: Determiner, variant: String?) {
        viewModelScope.launch {
            val newList = determinerList.value
            newList.replaceAll { if (it.title == determiner.title) it.copy(selectVariant = variant) else it }

            determinerList.update {
                newList
            }

        }
    }

    fun getHerbByParams() {
        var newList = mutableListOf<HerbEntity>()
        determinerList.value.dropLast(0).forEach { determiner ->
            newList += _allHerbs.value.filter { it.description.contains(determiner.selectVariant!!, true) }
            newList += _allHerbs.value.filter { it.title.contains(determiner.selectVariant!!, true) }
            println(newList)
        }
        resultHerbs.value = newList.toSet().toMutableList()
    }

}