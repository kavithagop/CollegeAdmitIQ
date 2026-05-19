package com.example.collegeadmitiq.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentProfileDao {

    @Query("SELECT * FROM student_profile WHERE id = 1")
    fun getProfile(): Flow<StudentProfileEntity?>

    @Query("SELECT * FROM student_profile WHERE id = 1")
    suspend fun getProfileOnce(): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: StudentProfileEntity)

    @Query("UPDATE student_profile SET isOnboardingComplete = 1 WHERE id = 1")
    suspend fun completeOnboarding()
}