package com.example.collegeadmitiq.di

import android.content.Context
import androidx.room.Room
import com.example.collegeadmitiq.data.local.AISuggestionDao
import com.example.collegeadmitiq.data.local.AppDatabase
import com.example.collegeadmitiq.data.local.PortfolioItemDao
import com.example.collegeadmitiq.data.local.StudentProfileDao
import com.example.collegeadmitiq.data.remote.GeminiService
import com.example.collegeadmitiq.data.repository.CollegeAdmitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "college_admit_iq_db"
        ).build()
    }

    @Provides
    fun provideStudentProfileDao(db: AppDatabase): StudentProfileDao = db.studentProfileDao()

    @Provides
    fun providePortfolioItemDao(db: AppDatabase): PortfolioItemDao = db.portfolioItemDao()

    @Provides
    fun provideAISuggestionDao(db: AppDatabase): AISuggestionDao = db.aiSuggestionDao()

    @Provides
    @Singleton
    fun provideGeminiService(): GeminiService = GeminiService()

    @Provides
    @Singleton
    fun provideRepository(
        studentProfileDao: StudentProfileDao,
        profileItemDao: PortfolioItemDao,
        aiSuggestionDao: AISuggestionDao,
        geminiService: GeminiService,
    ): CollegeAdmitRepository = CollegeAdmitRepository(
        studentProfileDao,
        profileItemDao,
        aiSuggestionDao,
        geminiService
    )
}