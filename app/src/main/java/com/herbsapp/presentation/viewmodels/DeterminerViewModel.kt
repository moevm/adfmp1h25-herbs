package com.herbsapp.presentation.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herbsapp.R
import com.herbsapp.data.repository.AppRepository
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.Determiner
import com.herbsapp.presentation.ui.DeterminerImaged
import com.herbsapp.presentation.ui.ResultHerb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode

class DeterminerViewModel(val repository: AppRepository, context: Context) : ViewModel() {

    private val _allHerbs = MutableStateFlow<List<HerbEntity>>(listOf())

    val resultHerbs: MutableStateFlow<MutableList<ResultHerb>> = MutableStateFlow(mutableListOf())
    val currentHerbID = MutableStateFlow<Int>(0)
    val determinerList = MutableStateFlow(
        mutableStateListOf(
            Determiner(
                context.getString(R.string.determiner_question_1),
                listOf(
                    context.getString(R.string.determiner_question_1_var1),
                    context.getString(R.string.determiner_question_1_var2),
                    context.getString(R.string.determiner_question_1_var3),
                    context.getString(R.string.idk),
                ),
                null
            ),
            Determiner(
                context.getString(R.string.determiner_question_3),
                listOf(
                    context.getString(R.string.determiner_question_3_var1),
                    context.getString(R.string.determiner_question_3_var2),
                    context.getString(R.string.determiner_question_3_var3),
                    context.getString(R.string.idk),
                ),
                null
            ),
            Determiner(
                context.getString(R.string.determiner_question_4),
                listOf(
                    context.getString(R.string.determiner_question_4_var1),
                    context.getString(R.string.determiner_question_4_var2),
                    context.getString(R.string.idk),
                ),
                null
            ),
        )
    )

    val determinerListWithImages = MutableStateFlow(
        mutableStateListOf<DeterminerImaged>(
            DeterminerImaged(
                context.getString(R.string.determiner_question_5),
                listOf(
                    context.getString(R.string.determiner_question_5_var2),
                    context.getString(R.string.determiner_question_5_var3),
                    context.getString(R.string.determiner_question_5_var1),
                    context.getString(R.string.determiner_question_5_var4),
                ),
                listOf(
                    R.drawable.img_leaf_shape_2,
                    R.drawable.img_leaf_shape_3,
                    R.drawable.img_leaf_shape_1,
                    R.drawable.img_leaf_shape_4,
                ),
                null
            ),
            DeterminerImaged(
                context.getString(R.string.determiner_question_6),
                listOf(
                    context.getString(R.string.determiner_question_6_var2),
                    context.getString(R.string.determiner_question_6_var1),
                    context.getString(R.string.determiner_question_6_var3),
                    context.getString(R.string.determiner_question_6_var4),
                ),
                listOf(
                    R.drawable.img_leaf_veinig_2,
                    R.drawable.img_leaf_veinig_1,
                    R.drawable.img_leaf_veinig_3,
                    R.drawable.img_leaf_veinig_4,
                ),
                null
            ),
        ))

    init {
        updateData()
    }

    fun updateData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHerbs().collect {
                _allHerbs.value = it
            }
        }
    }

    fun clearSelect() {
        determinerList.update {
            it.apply { replaceAll { if (it.selectVariant != null)  it.copy(selectVariant = null) else it } }
        }
        determinerListWithImages.update {
            it.apply { replaceAll { if (it.selectVariant != null)  it.copy(selectVariant = null) else it } }
        }
        currentHerbID.value = 0
        resultHerbs.value = mutableListOf()
    }

    fun nextHerb(onEndList: () -> Unit) {
        if (resultHerbs.value.size > currentHerbID.value + 1) {
            currentHerbID.value++
        } else {
//            determinerList.update {
//                it.apply { replaceAll { it.copy(selectVariant = null) } }
//            }
//            determinerListWithImages.update {
//                it.apply { replaceAll { it.copy(selectVariant = null) } }
//            }
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

    fun choose(determiner: DeterminerImaged, variant: String?) {
        viewModelScope.launch {
            val newList = determinerListWithImages.value
            newList.replaceAll { if (it.title == determiner.title) it.copy(selectVariant = variant) else it }

            determinerListWithImages.update {
                newList
            }
        }
    }

    fun getHerbByParams(context: Context) {
        resultHerbs.value = mutableListOf()
        var newList = mutableListOf<HerbEntity>()

        if (determinerList.value.first().selectVariant!! != "") {
            newList += _allHerbs.value.filter { it.mClass.lowercase() == determinerList.value.first().selectVariant!!.lowercase() }
        }

        if (determinerList.value[1].selectVariant!! != "") {
            newList += (if (newList.isEmpty()) _allHerbs.value else newList).filter {
                it.description.contains(
                    determinerList.value[1].selectVariant!!, true
                )
            }.toMutableList()
        }
        if (determinerList.value.last().selectVariant == determinerList.value.last().variants.first()) {
            newList += (if (newList.isEmpty()) _allHerbs.value else newList).filter { it.description.contains("запах", true) }.toMutableList()
        }

        determinerListWithImages.value.forEach { determiner ->
            if (determiner.selectVariant != "") {
                newList += (if (newList.isEmpty()) _allHerbs.value else newList).filter {
                    it.description.contains(
                        determiner.selectVariant!!.take(5),
                        true
                    )
                }.toMutableList()
            }
        }

        if (!newList.isNullOrEmpty()) {
            newList.sortBy { it.id }

            var percent = 1f
            var prevId = newList.first().id
            newList.forEach {
                if (prevId == it.id) {
                    percent++
                } else {
                    resultHerbs.value += ResultHerb(newList.find { it.id == prevId }!!,
                        ((percent / (determinerList.value.size + determinerListWithImages.value.size)) * 100).toBigDecimal()
                            .setScale(2, RoundingMode.FLOOR).toFloat()
                    )
                    prevId = it.id
                    percent = 1f
                }
            }
            resultHerbs.value.sortByDescending { it.similarPerc }
        }
    }

    fun String.isIdkEmpty(context: Context): String =
        if (this == context.getString(R.string.idk)) "" else this

}