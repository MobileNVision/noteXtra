package com.mobilenvision.notextra.data.local.db


import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.data.model.db.User
import java.util.UUID
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDbHelper : DbHelper {

    private var mAppDatabase: AppDatabase? = null
    private val executor = Executors.newSingleThreadExecutor()

    @Inject
    constructor(appDatabase: AppDatabase) {
        this.mAppDatabase = appDatabase
    }
    
    override fun getAllUsers(callback: DbCallback<List<User?>>) {
        executor.execute {
            try {
                val users = mAppDatabase!!.userDao()?.loadAllUser()
                users?.let { callback.onSuccess(it) }
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun getAllNotes(callback: DbCallback<List<Note>>) {
        executor.execute {
            try {
                val apps = mAppDatabase!!.noteDao()?.getAllNotes()
                apps?.let { callback.onSuccess(it) }
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun getUnSynchronized(callback: DbCallback<List<Note>>) {
        executor.execute {
            try {
                val apps = mAppDatabase!!.noteDao()?.getUnSynchronizedNotes()
                apps?.let { callback.onSuccess(it) }
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }


    override fun insertUser(user: User, callback: DbCallback<Boolean>) {
        executor.execute {
        try {
            mAppDatabase!!.userDao()?.insertUser(user)
            callback.onSuccess(true)
        } catch (e: Exception) {
            callback.onError(e)
        }
    }
    }

    override fun loadNoteById(noteIds : String, callback: DbCallback<Note>) {
        executor.execute {
            try {
                val apps = mAppDatabase!!.noteDao()?.loadNoteById(noteIds)
                apps?.let { callback.onSuccess(it) }
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }


    override fun insertNote(note: Note, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                note?.let { mAppDatabase!!.noteDao()?.insertNote(it) }
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }    }

    override fun getAllCategories(callback: DbCallback<List<Category>>) {
        executor.execute {
            try {
                val categoryIds = mAppDatabase!!.categoryDao()?.loadAllCategories()
                categoryIds?.let { callback.onSuccess(it as List<Category>) }
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun deleteCategory(category: Category?, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                category?.let { mAppDatabase!!.categoryDao()?.deleteCategory(it) }
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun deleteCategoryByName(categoryName: String?, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                categoryName?.let { mAppDatabase!!.categoryDao()?.deleteCategoryByName(it) }
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun loadCategoryById(categoryIds: String, callback: DbCallback<Category>) {
        executor.execute {
            try {
                val categoryIds = mAppDatabase!!.categoryDao()?.loadCategoryById(categoryIds)
                categoryIds?.let { callback.onSuccess(it) }
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun insertCategory(category: Category, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                category?.let { mAppDatabase!!.categoryDao()?.insertCategory(it) }
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun updateCategory(
        newCategoryName: String,
        exCategoryName: String,
        callback: DbCallback<Boolean>
    ) {
        executor.execute {
            try {
                mAppDatabase!!.categoryDao().updateCategory(newCategoryName,exCategoryName)
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun updateCategoryOfNote(
        noteId: String,
        categoryName: String?,
        callback: DbCallback<Boolean>
    ) {
        executor.execute {
            try {
                mAppDatabase!!.noteDao().updateCategory(noteId,categoryName)
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun updateIsSynchronized(noteId: String, isSynchronized: Boolean, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                mAppDatabase!!.noteDao().updateIsSynchronized(noteId, isSynchronized)
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun loadUserById(userId: String, callback: DbCallback<User>) {
        executor.execute {
            try {
                val user = mAppDatabase!!.userDao()?.loadUserById(userId)
                user?.let { callback.onSuccess(it) }
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun updateUser(user: User, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                mAppDatabase!!.userDao().updateUser(user)
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun updateNote(note: Note, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                mAppDatabase!!.noteDao().updateNote(note)
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun deleteNote(note: Note?, callback: DbCallback<Boolean>) {
        executor.execute {
            try {
                note?.let { mAppDatabase!!.noteDao()?.deleteNote(it) }
                callback.onSuccess(true)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }


}