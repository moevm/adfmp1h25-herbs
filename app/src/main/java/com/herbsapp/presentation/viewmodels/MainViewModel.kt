package com.herbsapp.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.herbsapp.R
import com.herbsapp.data.repository.AppRepository
import com.herbsapp.data.room.entity.ElementEntity
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.Sign
import com.herbsapp.presentation.ui.SignValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.database.getValue as getValue1

class MainViewModel(val applicationContext: Context, val repository: AppRepository) : ViewModel() {

    private val rootSignsList =
        mutableStateListOf<Sign>(
            Sign(
                title = applicationContext.getString(R.string.sign_all),
                R.drawable.ico_all,
                true,
            ),
            Sign(
                title = applicationContext.getString(R.string.sign_taste),
                R.drawable.ico_taste,
                false,
            ),
            Sign(
                title = applicationContext.getString(R.string.sign_class),
                R.drawable.ico_class,
                false,
            ),
            Sign(
                title = applicationContext.getString(R.string.sign_genus),
                R.drawable.ico_genus,
                false,
            ),
            Sign(
                title = applicationContext.getString(R.string.sign_family),
                R.drawable.ico_family,
                false,
            ),
        )

    private val _herbsList = MutableStateFlow<List<HerbEntity>>(listOf())
    val herbsList = _herbsList.asStateFlow()

    private val _searchList = MutableStateFlow<List<HerbEntity>>(mutableStateListOf())

    val searchList = _searchList.asStateFlow()
    val searchEntry = MutableStateFlow<String>("")

    val sortHerbs : MutableStateFlow<String?> = MutableStateFlow(null)

    private val _signsList = MutableStateFlow<SnapshotStateList<Sign>>(rootSignsList)

    val signsList = _signsList.asStateFlow()
    private val _elementsList = MutableStateFlow<SnapshotStateList<ElementEntity>>(
        mutableStateListOf()
    )

    val elementsList = _elementsList.asStateFlow()

    init {
        getData()
    }

    var rootClassList: SnapshotStateList<ElementEntity> = mutableStateListOf()
    var rootGenusList: SnapshotStateList<ElementEntity> = mutableStateListOf()
    var rootTasteList: SnapshotStateList<ElementEntity> = mutableStateListOf()
    var rootFamilyList: SnapshotStateList<ElementEntity> = mutableStateListOf()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHerbs().collect { herbs ->
                if (!sortHerbs.value.isNullOrEmpty()) {
                    _herbsList.value = sortHerbs.value!!.getSortValue().sortedBySortValue(herbs)
                } else {
                    _herbsList.value = herbs
                }
            }

            herbsList.value.forEachIndexed() { index, herbEntity ->
                rootClassList += ElementEntity(0,0,herbEntity.mClass,false)
                rootGenusList += ElementEntity(0,0,herbEntity.genus,false)
                rootTasteList += ElementEntity(0,0,herbEntity.taste,false)
                rootFamilyList += ElementEntity(0,0,herbEntity.family,false)
            }

            rootClassList = rootClassList.distinctBy { it.title }.toMutableStateList()
            rootGenusList = rootGenusList.distinctBy { it.title }.toMutableStateList()
            rootTasteList = rootTasteList.distinctBy { it.title }.toMutableStateList()
            rootFamilyList = rootFamilyList.distinctBy { it.title }.toMutableStateList()
            search()
        }
    }

    fun sort(sort: String) {
        sortHerbs.value = sort
        getData()
    }

    fun String.getSortValue(): SortValue {
       return when (this) {
           applicationContext.getString(R.string.sort_name) -> SortValue.Name
           applicationContext.getString(R.string.sort_default) -> SortValue.Default
           applicationContext.getString(R.string.sort_views) -> SortValue.Views
           applicationContext.getString(R.string.sort_rating) -> SortValue.Rating
           else -> SortValue.Default
        }
    }

    fun SortValue.sortedBySortValue(mherbsList: List<HerbEntity>): List<HerbEntity> {
        return when (this) {
            SortValue.Default -> mherbsList.sortedBy { it.id }
            SortValue.Views -> mherbsList.sortedBy { it.views }.reversed()
            SortValue.Rating -> mherbsList.sortedBy { it.rating }.reversed()
            SortValue.Name -> mherbsList.sortedBy { it.name }
        }
    }

    sealed class SortValue {
        object Default: SortValue()
        object Name: SortValue()
        object Views: SortValue()
        object Rating: SortValue()
    }



    fun chooseSign(sign: Sign) {
        val newList: MutableList<Sign> = mutableListOf()

        _signsList.value.forEach {
            if (it.isChoose && it != sign) {
                newList += it.copy(isChoose = false)
            } else {
                if (it.title == sign.title) {
                    newList += it.copy(isChoose = true, selectedElement = sign.selectedElement)
                } else {
                    newList += it
                }
            }
        }
        if (sign.getSignValueBySign() == SignValue.All) {
            newList.replaceAll { if (it.selectedElement != null) it.copy(selectedElement = null, isChoose = false) else it }
        }

        _signsList.update {
            newList.toMutableStateList()
        }
        showElementsBySign(sign)
    }

    fun search() {
        var classs: String? = null
        var taste: String? = null
        var genus: String? = null
        var family: String? = null

        signsList.value.forEach {
            if (it.selectedElement != null) {
                when (it.getSignValueBySign()) {
                    SignValue.All -> {}
                    SignValue.Class -> classs = it.selectedElement!!.title

                    SignValue.Genus -> genus = it.selectedElement!!.title

                    SignValue.Taste -> taste = it.selectedElement!!.title

                    SignValue.Family -> family = it.selectedElement!!.title
                }
            }
        }

        _searchList.value = herbsList.value.filter {
            ((((it.name.contains(searchEntry.value, ignoreCase = true)
                    && it.mClass == (classs ?: it.mClass)
                    && it.taste == (taste ?: it.taste)
                    && it.genus == (genus ?: it.genus)
                    && it.family == (family ?: it.family)))))
        }
    }

    fun showElementsBySign(sign: Sign) {
        when (sign.getSignValueBySign()) {
            SignValue.All -> _elementsList.value = mutableStateListOf()
            SignValue.Class -> _elementsList.value = rootClassList
            SignValue.Genus -> _elementsList.value = rootGenusList
            SignValue.Taste -> _elementsList.value = rootTasteList
            SignValue.Family -> _elementsList.value = rootFamilyList
        }

        if (sign.selectedElement != null) {
            val newList = mutableStateListOf<ElementEntity>()
            _elementsList.value.forEach {
                if (it.title == sign.selectedElement!!.title) {
                    newList += sign.selectedElement!!
                } else {
                    newList += it
                }
            }
            _elementsList.update {
                newList
            }
        }
    }

    fun Sign.getSignValueBySign(): SignValue =
        when (this.title) {
            applicationContext.getString(R.string.sign_all) -> SignValue.All
            applicationContext.getString(R.string.sign_class) -> SignValue.Class
            applicationContext.getString(R.string.sign_genus) -> SignValue.Genus
            applicationContext.getString(R.string.sign_taste) -> SignValue.Taste
            applicationContext.getString(R.string.sign_family) -> SignValue.Family
            else -> SignValue.All
        }

    fun chooseElement(element: ElementEntity) {
        val newlist = signsList.value.find { it.isChoose }
        newlist!!.selectedElement = if (element.isChoose) element else null
        chooseSign(newlist)
    }

    fun updateHerb(herbEntity: HerbEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateHerb(herbEntity)
            getData()
        }
    }

    fun clearSearch() {
        searchEntry.value = ""
        if (!signsList.value.isNullOrEmpty()) {
            chooseSign(signsList.value.find { it.getSignValueBySign() == SignValue.All }!!)
            search()
        }
    }

}