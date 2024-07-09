package com.mobilenvision.notextra.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mobilenvision.notextra.data.local.db.NoteTypeConverters
import java.io.Serializable

@TypeConverters(NoteTypeConverters::class)
@Entity(tableName = "notes")
class Note : Serializable {
    @ColumnInfo(name = "created_at")
    var createdAt: String? = null

    @PrimaryKey
    var id: String = ""
    var text: String? = null
    var updatedTime: String? = null
    var title: String? = null
    var isSynchronized: Boolean? = false
    var category: String? = null
    var version: Long? = null
    var userId: String? = null
    var priority: String? = null
    var reminderTime: String? = null

    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    constructor()

    constructor(title: String, text: String, id: String){
        this.id = id
        this.title = title
        this.text = text
    }

    constructor(
        noteId: String,
        title: String?,
        text: String,
        category: String?,
        reminderTime: String?,
        isSynchronized: Boolean? = false,
        updatedTime: String,
        version: Long?,
        userId: String?,
        priority: String?
    ) {
        this.id=noteId
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