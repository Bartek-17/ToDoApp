package com.example.todoapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.Task
import com.example.todoapp.ui.theme.ToDoAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val categories = listOf("Personal", "Work", "Shopping", "Other")

private fun formatDateTime(date: Date): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    onTaskCompletedChange: (Task, Boolean) -> Unit,
    onTaskClick: (Task) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var hideCompleted by remember { mutableStateOf(false) }

    val filteredTasks = tasks.filter {
        (it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)) &&
                (!hideCompleted || !it.isCompleted)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { hideCompleted = !hideCompleted }
            ) {
                Checkbox(
                    checked = hideCompleted,
                    onCheckedChange = { hideCompleted = it }
                )
                Text("Hide Done")
            }
        }
        LazyColumn {
            items(filteredTasks) { task ->
                TaskItem(
                    task = task,
                    onCompletedChange = { onTaskCompletedChange(task, it) },
                    onClick = { onTaskClick(task) }
                )
            }
        }
    }
}

@Composable
internal fun TaskItem(task: Task, onCompletedChange: (Boolean) -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCompletedChange
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
            )
            if (task.category.isNotBlank()) {
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Due: ${formatDateTime(task.dueTime)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (task.isNotificationEnabled) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notification Enabled",
                modifier = Modifier.padding(start = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdd: (Task) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories.first()) }
    var dueTime by remember { mutableStateOf(Date()) }
    var isNotificationEnabled by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueTime.time)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dueTime = Date(millis)
                    }
                    showDatePicker = false
                    showTimePicker = true 
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = dueTime
                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(Calendar.MINUTE, timePickerState.minute)
                    dueTime = cal.time
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                CategoryDropdown(selectedCategory = category, onCategorySelected = { category = it })
                Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("Due: ${formatDateTime(dueTime)}") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable Notification")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(checked = isNotificationEnabled, onCheckedChange = { isNotificationEnabled = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newTask = Task(
                        title = title,
                        description = description,
                        creationTime = Date(),
                        dueTime = dueTime,
                        isCompleted = false,
                        isNotificationEnabled = isNotificationEnabled,
                        category = category,
                        attachments = emptyList()
                    )
                    onTaskAdd(newTask)
                }
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onTaskUpdate: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit
) {
    var title by remember(task) { mutableStateOf(task.title) }
    var description by remember(task) { mutableStateOf(task.description) }
    var category by remember(task) { mutableStateOf(task.category.ifBlank { categories.first() }) }
    var dueTime by remember(task) { mutableStateOf(task.dueTime) }
    var isNotificationEnabled by remember(task) { mutableStateOf(task.isNotificationEnabled) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueTime.time)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dueTime = Date(millis)
                    }
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = dueTime
                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(Calendar.MINUTE, timePickerState.minute)
                    dueTime = cal.time
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                CategoryDropdown(selectedCategory = category, onCategorySelected = { category = it })
                Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) { Text("Due: ${formatDateTime(dueTime)}") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable Notification")
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(checked = isNotificationEnabled, onCheckedChange = { isNotificationEnabled = it })
                }
            }
        },
        confirmButton = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { onTaskDelete(task) }) { Text("Delete") }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) { Text("Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    val updatedTask = task.copy(
                        title = title,
                        description = description,
                        category = category,
                        dueTime = dueTime,
                        isNotificationEnabled = isNotificationEnabled,
                        attachments = emptyList()
                    )
                    onTaskUpdate(updatedTask)
                }) { Text("Save") }
            }
        },
        dismissButton = null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(selectedCategory: String, onCategorySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedCategory, onValueChange = {}, readOnly = true, label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(text = { Text(category) }, onClick = { onCategorySelected(category); expanded = false })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ToDoAppTheme {
        val task = Task(id = 1, title = "Test Task", description = "desc", creationTime = Date(), dueTime = Date(), isCompleted = false, isNotificationEnabled = true, category = "Personal")
        TaskItem(task = task, onCompletedChange = {}, onClick = {})
    }
}
