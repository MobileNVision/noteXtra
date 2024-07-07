package com.mobilenvision.notextra.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mobilenvision.notextra.data.model.db.User


@Dao
interface UserDao {
    @Delete
    fun deleteUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User)

    @Query("SELECT * FROM users")
    fun loadAllUser(): List<User>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun loadUserById(userId: String): User

    @Update
    fun updateUser(user: User)
}
