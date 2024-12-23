package com.example.travelupa.database.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.travelupa.database.dao.ImageDao
import com.example.travelupa.database.entity.ImageEntity

@Database(entities = [ImageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}




