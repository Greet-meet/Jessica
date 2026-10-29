package com.adspro.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adspro.app.data.*

class SmsSyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).smsOutboxDao()
        var allSuccessful = true
        for (sms in dao.getAll()) {
            val payload = mapOf("sender" to sms.sender, "body" to sms.body, "timestamp" to sms.timestamp)
            if (SupabaseClient.insert("sms_logs", payload)) {
                dao.deleteById(sms.id)
            } else { allSuccessful = false }
        }
        return if (allSuccessful) Result.success() else Result.retry()
    }
}
