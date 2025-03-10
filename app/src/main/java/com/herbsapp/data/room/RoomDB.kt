package com.herbsapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.herbsapp.data.room.converters.Converters
import com.herbsapp.data.room.dao.RoomDao
import com.herbsapp.data.room.entity.ElementEntity
import com.herbsapp.data.room.entity.HerbEntity

@TypeConverters(Converters::class)
@Database(version = 1, entities = [HerbEntity::class, ElementEntity::class], exportSchema = false)
abstract class RoomDB: RoomDatabase() {
    abstract fun getDao(): RoomDao

}