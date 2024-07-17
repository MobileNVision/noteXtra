package com.mobilenvision.notextra.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mobilenvision.notextra.utils.Converters
import java.io.Serializable

@Entity(tableName = "daily")
@TypeConverters(Converters::class)
class Daily : Serializable {
    @ColumnInfo(name = "created_at")
    var createdAt: String? = null

    @PrimaryKey
    var id: String = ""
    var text: String? = null
    var day: String? = null
    var isSynchronized: Boolean? = false
    var images: List<String>? = null
    var userId: String? = null

    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null
    constructor()
    constructor(id: String,description: String, day: String, images: List<String>, userId: String?){
        this.id = id
        this.text = description
        this.day = day
        this.images = images
        this.userId = userId
    }


}