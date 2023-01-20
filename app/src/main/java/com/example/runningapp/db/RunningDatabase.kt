package com.example.runningapp.db

import androidx.room.*

@Database(entities = [Run::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunningDatabase:RoomDatabase() {
    abstract fun getRunDao(): RunDao

}