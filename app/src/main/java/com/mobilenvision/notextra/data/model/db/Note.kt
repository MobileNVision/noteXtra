package com.mobilenvision.notextra.data.model.db

import androidx.annotation.NonNull
import com.mobilenvision.notextra.data.local.db.NoteTypeConverters
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable
import java.util.UUID

@TypeConverters(NoteTypeConverters::class)
@Entity(tableName = "notes")
class Note : Serializable {
    @ColumnInfo(name = "created_at")
    var createdAt: String? = null

    @PrimaryKey
    @NonNull
    var id: String = UUID.randomUUID().toString()
    var text: String? = null
    var updatedTime: String? = null
    var title: String? = null
    var isSynchronized: Boolean? = false
    var category: String? = null
    var version: Int? = null
    var userId: String? = null
    var priority: String? = null

    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    @ColumnInfo(name = "reminder_time")
    var reminderTime: String? = null

    constructor(
        title: String?,
        text: String,
        category: String?,
        reminderTime: String?,
        isSynchronized: Boolean? = false,
        updatedTime: String,
        version: Int?,
        userId: String?,
        priority: String?
    ) {
        this.createdAt = createdAt
        this.id = id
        this.text = text
        this.updatedTime = updatedTime
        this.title = title
        this.isSynchronized = isSynchronized
        this.category = category
        this.version = version
        this.reminderTime = reminderTime
        this.userId = userId
        this.priority = priority
    }
}