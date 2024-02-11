package com.example.vishavjit_harika

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseEntryRepository(private val exerciseEntryDatabaseDao: ExerciseEntryDatabaseDao) {

    val allEntries: LiveData<List<ExerciseEntry>> = exerciseEntryDatabaseDao.getAllEntries()

    suspend fun insert(entry: ExerciseEntry) {
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.insert(entry)
        }
    }

    suspend fun get(key: Long): ExerciseEntry = withContext(Dispatchers.IO) {
        exerciseEntryDatabaseDao.get(key)
    }

    fun delete(key: Long) {
        CoroutineScope(IO).launch {
            exerciseEntryDatabaseDao.delete(key)
        }
    }
}