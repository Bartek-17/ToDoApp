package com.example.todoapp

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.todoapp.data.Task
import com.example.todoapp.database.SettingsRepository
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)
    private val settingsRepository = SettingsRepository(context)

    fun scheduleNotification(task: Task) {
        // Cancel any previous notifications for this task
        cancelNotification(task)

        if (task.isNotificationEnabled) {
            val leadTime = settingsRepository.getNotificationLeadTime().toLong()
            val delay = task.dueTime.time - System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(leadTime)

            // Schedule a notification if the due time is in the future
            if (delay > 0) {
                val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(workDataOf(
                        "task_title" to task.title,
                        "task_id" to task.id
                    ))
                    .addTag(task.id.toString()) // Tag the work with the task ID for cancellation
                    .build()

                workManager.enqueue(notificationWork)
            }
        }
    }

    fun cancelNotification(task: Task) {
        workManager.cancelAllWorkByTag(task.id.toString())
    }
}