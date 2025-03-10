package com.herbsapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.herbsapp.data.room.converters.Converters

@Entity
data class HerbEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val name: String,
    val title: String,
    val description: String,
    val city: String,
    val views: Int,
    val rating: Float,

    @TypeConverters(Converters::class)
    val imageURL: List<String>,

    val mClass: String,
    val family: String,
    val taste: String,
    val genus: String,

    val isLiked: Boolean,
)

