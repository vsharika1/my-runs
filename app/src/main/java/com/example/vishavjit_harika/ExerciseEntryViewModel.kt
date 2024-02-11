package com.example.vishavjit_harika

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExerciseEntryViewModel(private val repository: ExerciseEntryRepository) : ViewModel() {
    val allEntries: LiveData<List<ExerciseEntry>> = repository.allEntries

    fun insert(entry: ExerciseEntry) {
        viewModelScope.launch {
            repository.insert(entry)
        }
    }

    suspend fun get(id: Long): ExerciseEntry? {
        return repository.get(id)
    }
}

class ExerciseEntryViewModelFactory(private val repository: ExerciseEntryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseEntryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}