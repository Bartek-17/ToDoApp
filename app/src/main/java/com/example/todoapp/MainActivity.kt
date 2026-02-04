package com.example.todoapp

import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.todoapp.data.Task
import com.example.todoapp.ui.AddTaskDialog
import com.example.todoapp.ui.EditTaskDialog
import com.example.todoapp.ui.SettingsScreen
import com.example.todoapp.ui.TaskScreen
import com.example.todoapp.ui.TaskViewModel
import com.example.todoapp.ui.theme.ToDoAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // handle permission requests - launch the permission dialog
    // and receive the result (granted or denied) in the lambda
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
    }

    private lateinit var notificationScheduler: NotificationScheduler

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        notificationScheduler = NotificationScheduler(this)
        requestNotificationPermission()

        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                val taskViewModel: TaskViewModel = viewModel()
                val tasks by taskViewModel.allTasks.collectAsState(initial = emptyList())
                var showAddTaskDialog by remember { mutableStateOf(false) }
                var editingTask by remember { mutableStateOf<Task?>(null) }
                var showSettings by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                // --- Deep Link Handling ---
                // Check if the app was opened from a notification tap
                val taskId = intent.getIntExtra("task_id", -1)
                if (taskId != -1 && tasks.isNotEmpty()) {
                    // LaunchedEffect will only trigger when taskId change
                    LaunchedEffect(tasks) {
                        editingTask = tasks.find { it.id == taskId }
                        // Reset the intent extra to prevent from re-triggering when activity is recreated
                        intent.removeExtra("task_id") 
                    }
                }

                // Navigation logic - show SettingsScreen or the main Scaffold
                if (showSettings) {
                    SettingsScreen(onBack = {
                        showSettings = false
                        // When returning from settings, reschedule all active notifications
                        // to ensure the new lead time is applied
                        tasks.filter { it.isNotificationEnabled && !it.isCompleted }.forEach {
                            notificationScheduler.scheduleNotification(it)
                        }
                    })
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                title = { Text("ToDo App") },
                                actions = {
                                    IconButton(onClick = { showSettings = true }) {
                                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                                Icon(Icons.Filled.Add, contentDescription = "Add Task")
                            }
                        }
                    ) { innerPadding ->
                        TaskScreen(
                            tasks = tasks,
                            modifier = Modifier.padding(innerPadding),
                            onTaskCompletedChange = { task, isCompleted ->
                                val updatedTask = task.copy(isCompleted = isCompleted)
                                taskViewModel.update(updatedTask)
                                notificationScheduler.scheduleNotification(updatedTask)
                            },
                            onTaskClick = { task ->
                                editingTask = task
                            }
                        )

                        if (showAddTaskDialog) {
                            AddTaskDialog(
                                onDismiss = { showAddTaskDialog = false },
                                onTaskAdd = { task ->
                                    scope.launch {
                                        val newId = taskViewModel.insert(task)
                                        val newTask = task.copy(id = newId.toInt())
                                        notificationScheduler.scheduleNotification(newTask)
                                    }
                                    showAddTaskDialog = false
                                }
                            )
                        }

                        editingTask?.let { task ->
                            EditTaskDialog(
                                task = task,
                                onDismiss = { editingTask = null },
                                onTaskUpdate = { updatedTask ->
                                    taskViewModel.update(updatedTask)
                                    notificationScheduler.scheduleNotification(updatedTask)
                                    editingTask = null
                                },
                                onTaskDelete = { taskToDelete ->
                                    taskViewModel.delete(taskToDelete)
                                    notificationScheduler.cancelNotification(taskToDelete)
                                    editingTask = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        // On Android 13 (TIRAMISU) and higher, runtime permission is required for notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission has already been granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                // If not, launch the permission request dialog
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}