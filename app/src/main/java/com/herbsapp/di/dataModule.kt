package com.herbsapp.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.herbsapp.data.repository.AppRepository
import com.herbsapp.data.room.RoomDB
import com.herbsapp.data.room.dao.RoomDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    fun provideDatabase(context: Context) =
        Room.databaseBuilder(context, RoomDB::class.java, "db")
            .build()
    fun provideDao(db: RoomDB) = db.getDao()

    single { provideDatabase(get()) }
    single { provideDao(get()) }
    single { FirebaseAuth.getInstance() }
    single { AppRepository(get(), get(), get()) }

}