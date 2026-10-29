package com.adspro.app.data

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

object SupabaseClient {
    private const val BASE_URL = "https://bqddohfszkqpvncpbrun.supabase.co"
    private const val REST_URL = "$BASE_URL/rest/v1"
    private const val STORAGE_URL = "$BASE_URL/storage/v1"
    private const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJxZGRvaGZzemtxcHZuY3BicnVuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTI2MDAwNjIsImV4cCI6MjEwODE3NjA2Mn0.J0uq4JPpPxoU0hc4ry5WglCV_HcTqshcHsl3oCJ7upA"

    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
    private val gson = Gson()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    fun insert(table: String, payload: Any): Boolean {
        val req = Request.Builder().url("$REST_URL/$table").post(gson.toJson(payload).toRequestBody(JSON))
            .addHeader("apikey", ANON_KEY).addHeader("Authorization", "Bearer $ANON_KEY")
            .addHeader("Content-Type", "application/json").addHeader("Prefer", "return=minimal").build()
        return try { client.newCall(req).execute().use { it.isSuccessful } } catch(e: Exception) { false }
    }

    fun uploadFile(bucket: String, path: String, file: File, mime: String): String? {
        val req = Request.Builder().url("$STORAGE_URL/object/$bucket/$path").post(file.asRequestBody(mime.toMediaType()))
            .addHeader("apikey", ANON_KEY).addHeader("Authorization", "Bearer $ANON_KEY").addHeader("x-upsert", "true").build()
        return try { client.newCall(req).execute().use { if (it.isSuccessful) "$STORAGE_URL/object/public/$bucket/$path" else null } } catch(e: Exception) { null }
    }

    fun listMedia(bucket: String): List<String> {
        val req = Request.Builder().url("$STORAGE_URL/object/list/$bucket").post(gson.toJson(mapOf("prefix" to "", "limit" to 100)).toRequestBody(JSON))
            .addHeader("apikey", ANON_KEY).addHeader("Authorization", "Bearer $ANON_KEY").build()
        return try {
            client.newCall(req).execute().use { res ->
                if (res.isSuccessful) gson.fromJson(res.body?.string(), Array<StorageItem>::class.java).map { "$STORAGE_URL/object/public/$bucket/${it.name}" } else emptyList()
            }
        } catch(e: Exception) { emptyList() }
    }
    private data class StorageItem(val name: String)
}
