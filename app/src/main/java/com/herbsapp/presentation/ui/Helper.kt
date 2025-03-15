package com.herbsapp.presentation.ui

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.herbsapp.data.room.entity.ElementEntity
import com.herbsapp.data.room.entity.HerbEntity

fun Context.imageLoader() = ImageLoader.Builder(this).components{ SvgDecoder.Factory()}.build()

data class Sign(
    val title: String,
    val icon: Int,
    val isChoose: Boolean,
    var selectedElement: ElementEntity? = null,
)

data class Determiner(
    val title: String,
    val variants: List<String>,
    var selectVariant: String? = null
)

data class DeterminerImaged(
    val title: String,
    val variants: List<String>,
    val images: List<Int>,
    var selectVariant: String? = null
)

data class ResultHerb(
    val herb: HerbEntity,
    val similarPerc: Float,
)

sealed class SignValue {
    object All: SignValue()
    object Class: SignValue()
    object Genus: SignValue()
    object Taste: SignValue()
    object Family: SignValue()
    object Veining: SignValue()
    object Shape: SignValue()
}

sealed class Resource<out R> {
    data class Success<out R>(val result: R) : Resource<R>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}