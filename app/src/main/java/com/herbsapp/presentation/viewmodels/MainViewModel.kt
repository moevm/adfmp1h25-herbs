package com.herbsapp.presentation.viewmodels

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.launch
import com.google.firebase.database.getValue as getValue1

class MainViewModel(val applicationContext: Context, val repository: AppRepository) : ViewModel() {

    private val rootSignsList =
        mutableListOf<Sign>(
            Sign(
                title = applicationContext.getString(R.string.sign_all),
                R.drawable.ico_all,
                true,
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
                title = applicationContext.getString(R.string.sign_taste),
                R.drawable.ico_taste,
                false,
            ),
            Sign(
                title = applicationContext.getString(R.string.sign_family),
                R.drawable.ico_family,
                false,
            ),
        )

    private val _herbsList = MutableStateFlow<List<HerbEntity>>(emptyList())

    val herbsList = _herbsList.asStateFlow()
    private val _searchList = MutableStateFlow<List<HerbEntity>>(emptyList())

    val searchList = _searchList.asStateFlow()
    val searchEntry = MutableStateFlow<String>("")

    private val _signsList = MutableStateFlow<List<Sign>>(rootSignsList)

    val signsList = _signsList.asStateFlow()
    private val _elementsList = MutableStateFlow<List<ElementEntity>>(emptyList())

    val elementsList = _elementsList.asStateFlow()

    init {
        getData()
    }

    var rootClassList: List<ElementEntity> = listOf()
    var rootGenusList: List<ElementEntity> = listOf()
    var rootTasteList: List<ElementEntity> = listOf()
    var rootFamilyList: List<ElementEntity> = listOf()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getHerbs().collect { herbs ->
                _herbsList.value = herbs
            }

            repository.getElements().collect { elements ->
                rootClassList = elements.filter { it.signId == 1 }
                rootGenusList = elements.filter { it.signId == 2 }
                rootTasteList = elements.filter { it.signId == 3 }
                rootFamilyList = elements.filter { it.signId == 4 }
            }
        }
    }

    fun FirebaseConfigure() {
        val database = Firebase.database
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
    }

    fun FirebaseUpdate() {
        val database = Firebase.database
        val myRef = database.getReference("message")
        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue1<String>()
                Log.d("TAG", "Value is: " + value)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }

        })
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

        _signsList.value = newList
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
            SignValue.All -> _elementsList.value = emptyList()
            SignValue.Class -> _elementsList.value = rootClassList
            SignValue.Genus -> _elementsList.value = rootGenusList
            SignValue.Taste -> _elementsList.value = rootTasteList
            SignValue.Family -> _elementsList.value = rootFamilyList
        }

        if (sign.selectedElement != null) {
            val newList = mutableListOf<ElementEntity>()
            _elementsList.value.forEach {
                if (it.title == sign.selectedElement!!.title) {
                    newList += sign.selectedElement!!
                } else {
                    newList += it
                }
            }
            _elementsList.value = newList
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

}