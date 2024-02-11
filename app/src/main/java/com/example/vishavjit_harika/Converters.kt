package com.example.vishavjit_harika

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Converters {
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    @TypeConverter
    fun fromTimestamp(value: String?): Calendar? {
        return value?.let {
            val cal = Calendar.getInstance()
            cal.time = format.parse(value) ?: return null
            cal
        }
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): String? {
        return calendar?.let {
            format.format(it.time)
        }
    }

    @TypeConverter
    fun fromLatLngList(value: ArrayList<LatLng>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLatLngList(value: String): ArrayList<LatLng> {
        val listType = object : TypeToken<ArrayList<LatLng>>() {}.type
        return Gson().fromJson(value, listType)
    }
}