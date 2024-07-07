package com.mobilenvision.notextra.data.local.db

import androidx.room.TypeConverter

class NoteTypeConverters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }
    }
}