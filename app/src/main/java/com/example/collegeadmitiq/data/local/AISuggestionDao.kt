package com.example.collegeadmitiq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AISuggestionDao {

    @Query("SELECT * FROM ai_suggestions ORDER BY id DESC")
    fun getAllSuggestions(): Flow<List<AISuggestionEntity>>

    @Query("SELECT * FROM ai_suggestions WHERE isSaved = 1")
    fun getSavedSuggestions(): Flow<List<AISuggestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestions(suggestions: List<AISuggestionEntity>)

    @Query("UPDATE ai_suggestions SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSaved(id: Long, isSaved: Boolean)

    @Query("UPDATE ai_suggestions SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompleted(id: Long, isCompleted: Boolean)

    @Query("DELETE FROM ai_suggestions WHERE isSaved = 0")
    suspend fun clearUnsavedSuggestions()
}