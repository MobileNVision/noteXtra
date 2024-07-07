package com.mobilenvision.notextra.data.local.db

import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.data.model.db.User


interface DbHelper {
    fun getAllUsers(callback: DbCallback<List<User?>>)
    fun insertUser(user: User, callback: DbCallback<Boolean>)
    fun getAllNotes(callback: DbCallback<List<Note>>)
    fun getUnSynchronized(callback: DbCallback<List<Note>>)
    fun deleteNote(note: Note?,callback: DbCallback<Boolean>)
    fun updateNote(note: Note,callback: DbCallback<Boolean>)
    fun loadNoteById(noteIds: String, callback: DbCallback<Note>)
    fun insertNote(note: Note, callback: DbCallback<Boolean>)
    fun getAllCategories(callback: DbCallback<List<Category>>)
    fun deleteCategory(category: Category?,callback: DbCallback<Boolean>)
    fun deleteCategoryByName(categoryName: String?,callback: DbCallback<Boolean>)
    fun loadCategoryById(categoryIds: String, callback: DbCallback<Category>)
    fun insertCategory(categoryName: Category, callback: DbCallback<Boolean>)
    fun updateCategory(newCategoryName: String, exCategoryName: String, callback: DbCallback<Boolean>)
    fun updateCategoryOfNote(noteId: String,categoryName: String?, callback: DbCallback<Boolean>)
    fun updateIsSynchronized(noteId: String, isSynchronized: Boolean, callback: DbCallback<Boolean>)
    fun loadUserById(userId: String, callback: DbCallback<User>)
    fun updateUser(user: User, callback: DbCallback<Boolean>)
}