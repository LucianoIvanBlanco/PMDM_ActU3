package com.utad.ideasapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.utad.ideasapp.room.model.Detail
import com.utad.ideasapp.room.model.Idea


@Database(entities = [Idea::class, Detail::class], version = 1)
@TypeConverters(ImageTypeConverters::class)
abstract class IdeasDataBase : RoomDatabase() {
    abstract fun ideaDao(): IdeaDao

}