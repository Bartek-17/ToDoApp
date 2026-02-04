package com.example.todoapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.Task
import com.example.todoapp.database.AppDatabase
import com.example.todoapp.database.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// ViewModel class for the main task screen. Bridge between UI and data layer
class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    val allTasks: Flow<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }


    // A suspending function that inserts a task and returns its new ID
    // It uses withContext(Dispatchers.IO) to ensure the database operation happens on a background thread

    suspend fun insert(task: Task): Long {
        return withContext(Dispatchers.IO) {
            repository.insert(task)
        }
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }
}