package com.example.todoapp.database

import android.content.Context
import android.content.SharedPreferences

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getNotificationLeadTime(): Int {
        return prefs.getInt("notification_lead_time", 10) // Default to 10 minutes
    }

    fun setNotificationLeadTime(minutes: Int) {
        prefs.edit().putInt("notification_lead_time", minutes).apply()
    }
}