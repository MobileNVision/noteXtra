package com.mobilenvision.notextra.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User {
    @ColumnInfo(name = "created_at")
    var createdAt: String? = null

    @PrimaryKey
    var id: String
    var firstName: String? = null
    var lastName: String? = null
    var mail: String? = null
    var password: String? = null
    var profileImage: String? = null

    @ColumnInfo(name = "updated_at")
    var updatedAt: String? = null

    constructor(
        id: String,
        firstName: String,
        lastName: String?,
        mail: String?,
        password: String?,
        profileImage: String?
    ) {
        this.createdAt = createdAt
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
        this.mail = mail
        this.password = password
        this.profileImage = profileImage

    }
}