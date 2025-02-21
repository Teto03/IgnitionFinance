package com.unimib.ignitionfinance.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.local.utils.SyncStatus

class SyncQueueItemConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromDocument(document: Map<String, Any>): String = gson.toJson(document)

    @TypeConverter
    fun toDocument(documentString: String): Map<String, Any>? = gson.fromJson(documentString, object : TypeToken<Map<String, Any>>() {}.type)

    @TypeConverter
    fun fromStatus(status: SyncStatus): String = gson.toJson(status)

    @TypeConverter
    fun toStatus(statusString: String): SyncStatus = gson.fromJson(statusString, object : TypeToken<SyncStatus>() {}.type)
}