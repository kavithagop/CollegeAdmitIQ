package com.example.collegeadmitiq.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        StudentProfileEntity::class,
        PortfolioItemEntity::class,
        AISuggestionEntity::class
    ],
    version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun studentProfileDao(): StudentProfileDao
    abstract fun portfolioItemDao(): PortfolioItemDao
    abstract fun aiSuggestionDao(): AISuggestionDao
}