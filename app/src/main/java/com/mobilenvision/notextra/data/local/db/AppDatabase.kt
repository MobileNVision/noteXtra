package com.mobilenvision.notextra.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobilenvision.notextra.data.local.db.dao.CategoryDao
import com.mobilenvision.notextra.data.local.db.dao.NoteDao
import com.mobilenvision.notextra.data.local.db.dao.UserDao
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.data.model.db.User

@Database(entities = [User::class, Note::class, Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
     abstract fun noteDao(): NoteDao
     abstract fun userDao(): UserDao
     abstract fun categoryDao(): CategoryDao
}