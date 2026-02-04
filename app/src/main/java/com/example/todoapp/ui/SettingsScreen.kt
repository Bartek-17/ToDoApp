package com.example.todoapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todoapp.database.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    // Get the current context needed to initialize the SettingsRepository
    val context = LocalContext.current
    // remember so that the repository is created only once
    val settingsRepository = remember { SettingsRepository(context) }
    // State for the currently selected lead time, initialized from the repository
    var leadTime by remember { mutableStateOf(settingsRepository.getNotificationLeadTime()) }
    val leadTimeOptions = listOf(5, 10, 15, 30, 60)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                // Back button in the top app bar
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notification Lead Time")
                LeadTimeDropdown(leadTime, onLeadTimeSelected = {
                    leadTime = it
                    settingsRepository.setNotificationLeadTime(it)
                }, leadTimeOptions)
            }
        }
    }
}

// Dropdown menu for notification lead time
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTimeDropdown(selectedLeadTime: Int, onLeadTimeSelected: (Int) -> Unit, leadTimeOptions: List<Int>) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = "$selectedLeadTime minutes",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            leadTimeOptions.forEach { time ->
                DropdownMenuItem(
                    text = { Text("$time minutes") },
                    onClick = {
                        onLeadTimeSelected(time)
                        expanded = false
                    }
                )
            }
        }
    }
}
