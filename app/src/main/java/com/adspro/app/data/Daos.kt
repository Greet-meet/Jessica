package com.adspro.app.data
import androidx.room.*
@Dao interface SmsOutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(sms: SmsOutbox): Long
    @Query("SELECT * FROM sms_outbox ORDER BY timestamp ASC") suspend fun getAll(): List<SmsOutbox>
    @Query("DELETE FROM sms_outbox WHERE id = :id") suspend fun deleteById(id: Long)
}
@Dao interface NotificationOutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(n: NotificationOutbox): Long
    @Query("SELECT * FROM notification_outbox ORDER BY timestamp ASC") suspend fun getAll(): List<NotificationOutbox>
    @Query("DELETE FROM notification_outbox WHERE id = :id") suspend fun deleteById(id: Long)
}
