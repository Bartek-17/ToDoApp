package com.example.todoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    // Worker method is called by WorkManager on a background thread
    override fun doWork(): Result {
        // Retrieve the task title and ID from the input data passed to the worker
        val taskTitle = inputData.getString("task_title") ?: return Result.failure()
        val taskId = inputData.getInt("task_id", -1)

        // Create an intent that will open the MainActivity when the notification is tapped
        // Pass the task ID as an extra to enable deep linking
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("task_id", taskId)
        }

        // A PendingIntent is a token that you give to another application (like the NotificationManager)
        // which allows that application to execute a piece of your code
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // On Android 8.0 and higher, all notifications must be assigned to a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("task_reminders", "Task Reminders", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification using the NotificationCompat builder for backward compatibility
        val notification = NotificationCompat.Builder(applicationContext, "task_reminders")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Reminder")
            .setContentText("Your task \"$taskTitle\" is due soon.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Intent will fire when the user taps the notification
            .setAutoCancel(true) // Automatically removes the notification when the user taps it
            .build()

        // Display the notification
        notificationManager.notify(taskId, notification)

        return Result.success()
    }
}