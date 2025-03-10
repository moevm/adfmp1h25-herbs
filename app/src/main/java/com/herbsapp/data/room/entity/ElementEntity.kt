package com.herbsapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ElementEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val signId: Int,
    val title: String,
    val isChoose: Boolean
)