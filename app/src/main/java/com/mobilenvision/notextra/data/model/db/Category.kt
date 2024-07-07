package com.mobilenvision.notextra.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "categories", indices = [Index(value = ["name"], unique = true)])
class Category {
    @ColumnInfo(name = "created_at")
    var createdAt: String? = null

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String? = null

    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    constructor(name: String){
        this.name = name
    }
}