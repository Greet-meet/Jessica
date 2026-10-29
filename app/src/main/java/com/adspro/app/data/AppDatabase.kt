package com.adspro.app.data
import android.content.Context
import androidx.room.*
@Database(entities = [SmsOutbox::class, NotificationOutbox::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun smsOutboxDao(): SmsOutboxDao
    abstract fun notificationOutboxDao(): NotificationOutboxDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(c: Context) = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(c.applicationContext, AppDatabase::class.java, "ads_pro_database").build().also { INSTANCE = it }
        }
    }
}
