package com.example.vishavjit_harika

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExerciseEntryDatabaseDao {

    @Insert
    suspend fun insert(entry: ExerciseEntry)

    @Query("SELECT * FROM exercise_entry_table WHERE id = :key")
    suspend fun get(key: Long): ExerciseEntry

    @Query("SELECT * FROM exercise_entry_table ORDER BY id DESC")
    fun getAllEntries(): LiveData<List<ExerciseEntry>>

    @Query("DELETE FROM exercise_entry_table WHERE id = :key")
    suspend fun delete(key: Long)
}