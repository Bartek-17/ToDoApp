package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


// Entity definition for the Task entity
@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val creationTime: Date,
    val dueTime: Date,
    val isCompleted: Boolean,
    val isNotificationEnabled: Boolean,
    val category: String,
    val attachments: List<String> = emptyList()
)