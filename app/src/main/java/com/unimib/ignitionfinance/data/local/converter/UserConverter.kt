package com.unimib.ignitionfinance.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult

class UserConverter {
    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .create()

    @TypeConverter
    fun fromAuthData(value: AuthData?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toAuthData(value: String?): AuthData? {
        return value?.let {
            gson.fromJson(it, AuthData::class.java)
        }
    }

    @TypeConverter
    fun fromSettings(value: Settings?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSettings(value: String?): Settings? {
        return value?.let {
            gson.fromJson(it, Settings::class.java)
        }
    }

    @TypeConverter
    fun fromProductList(value: List<Product>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toProductList(value: String?): List<Product> {
        if (value == null) return emptyList()
        return try {
            val listType = object : TypeToken<List<Product>>() {}.type
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromDataset(value: List<DailyReturn>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toDataset(value: String?): List<DailyReturn> {
        if (value == null) return emptyList()
        return try {
            val listType = object : TypeToken<List<DailyReturn>>() {}.type
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromSimulationOutcome(value: Pair<List<SimulationResult>, Double?>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSimulationOutcome(value: String?): Pair<List<SimulationResult>, Double?>? {
        return value?.let {
            try {
                val type = object : TypeToken<Pair<List<SimulationResult>, Double?>>() {}.type
                gson.fromJson<Pair<List<SimulationResult>, Double?>>(it, type)
            } catch (e: Exception) {
                null
            }
        }
    }
}