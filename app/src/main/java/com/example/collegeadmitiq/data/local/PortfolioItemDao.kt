package com.example.collegeadmitiq.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioItemDao {

    @Query("SELECT * FROM portfolio_items ORDER BY startDate DESC")
    fun getAllItems(): Flow<List<PortfolioItemEntity>>

    @Query("SELECT * FROM portfolio_items WHERE category = :category ORDER BY startDate")
    fun getItemsByCategory(category: String): Flow<List<PortfolioItemEntity>>

    @Query("SELECT * FROM portfolio_items WHERE id = :id")
    fun getItemById(id: Long): PortfolioItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PortfolioItemEntity): Long

    @Update
    suspend fun updateItem(item: PortfolioItemEntity)

    @Delete
    suspend fun deleteItem(item: PortfolioItemEntity)

    @Query("SELECT COUNT(*) FROM portfolio_items")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM portfolio_items WHERE category = :category")
    fun getCountByCategory(category: String): Flow<Int>
}