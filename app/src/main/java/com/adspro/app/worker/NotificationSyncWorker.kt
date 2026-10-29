package com.adspro.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adspro.app.data.*

class NotificationSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).notificationOutboxDao()
        var allSuccessful = true
        for (notification in dao.getAll()) {
            val payload = mapOf("package_name" to notification.packageName, "title" to notification.title, "text" to notification.text, "timestamp" to notification.timestamp)
            if (SupabaseClient.insert("notifications", payload)) {
                dao.deleteById(notification.id)
            } else { allSuccessful = false }
        }
        return if (allSuccessful) Result.success() else Result.retry()
    }
}
