package com.herbsapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.herbsapp.data.room.entity.ElementEntity
import com.herbsapp.data.room.entity.HerbEntity

@Dao
interface RoomDao {

    @Upsert
    suspend fun upsertHerbs(herb: List<HerbEntity>)

    @Update
    suspend fun updateHerb(herb: HerbEntity)

    @Query("SELECT * FROM HerbEntity")
    fun getHerbs(): List<HerbEntity>

    @Query("SELECT * FROM HerbEntity WHERE id = :id")
    fun getHerbById(id: Int) : HerbEntity


    @Insert
    suspend fun insertElements(elements: List<ElementEntity>)

    @Query("SELECT * FROM ElementEntity")
    fun getElements(): List<ElementEntity>

}