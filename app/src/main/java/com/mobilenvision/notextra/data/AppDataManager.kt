package com.mobilenvision.notextra.data


import android.content.Context
import com.google.gson.Gson
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.local.db.DbHelper
import com.mobilenvision.notextra.data.local.prefs.PreferencesHelper
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.data.model.db.User
import javax.inject.Inject

class AppDataManager : DataManager {

    private var mGson: Gson
    private var mPreferencesHelper: PreferencesHelper
    private var mDbHelper: DbHelper
    private var mContext: Context

    @Inject
    constructor(
        context: Context,
        dbHelper: DbHelper,
        preferencesHelper: PreferencesHelper,
        gson: Gson
    ) {
        mContext = context
        mDbHelper = dbHelper
        mPreferencesHelper = preferencesHelper
        mGson = gson
    }


    override fun getAllUsers(callback: DbCallback<List<User?>>) {
        mDbHelper.getAllUsers(callback)
    }

    override fun getAllNotes(callback: DbCallback<List<Note>>) {
        mDbHelper.getAllNotes(callback)
    }

    override fun getUnSynchronized(callback: DbCallback<List<Note>>) {
        mDbHelper.getUnSynchronized(callback)
    }


    override fun insertUser(user: User, callback: DbCallback<Boolean>) {
        mDbHelper.insertUser(user, callback)
    }

    override fun loadNoteById(noteIds: String, callback: DbCallback<Note>) {
        mDbHelper.loadNoteById(noteIds, callback)
    }

    override fun insertNote(note: Note, callback: DbCallback<Boolean>) {
        mDbHelper.insertNote(note, callback)
    }

    override fun getAllCategories(callback: DbCallback<List<Category>>) {
        mDbHelper.getAllCategories(callback)
    }

    override fun deleteCategory(category: Category?, callback: DbCallback<Boolean>) {
        mDbHelper.deleteCategory(category,callback)
    }

    override fun deleteCategoryByName(categoryName: String?, callback: DbCallback<Boolean>) {
        mDbHelper.deleteCategoryByName(categoryName,callback)
    }

    override fun loadCategoryById(categoryIds: String, callback: DbCallback<Category>) {
        mDbHelper.loadCategoryById(categoryIds,callback)
    }

    override fun insertCategory(category: Category, callback: DbCallback<Boolean>) {
        mDbHelper.insertCategory(category,callback)
    }

    override fun updateCategory(
        newCategoryName: String,
        exCategoryName: String,
        callback: DbCallback<Boolean>
    ) {
        mDbHelper.updateCategory(newCategoryName,exCategoryName,callback)
    }

    override fun updateCategoryOfNote(
        noteId: String,
        categoryName: String?,
        callback: DbCallback<Boolean>
    ) {
        mDbHelper.updateCategoryOfNote(noteId, categoryName, callback)
    }

    override fun updateIsSynchronized(
        noteId: String,
        isSynchronized: Boolean,
        callback: DbCallback<Boolean>
    ) {
        mDbHelper.updateIsSynchronized(noteId,isSynchronized,callback)
    }

    override fun loadUserById(userId: String, callback: DbCallback<User>) {
        mDbHelper.loadUserById(userId, callback)
    }

    override fun updateUser(user: User, callback: DbCallback<Boolean>) {
        mDbHelper.updateUser(user, callback)
    }

    override fun deleteNote(note: Note?, callback: DbCallback<Boolean>) {
        mDbHelper.deleteNote(note, callback)
    }

    override fun updateNote(note: Note, callback: DbCallback<Boolean>) {
        mDbHelper.updateNote(note, callback)
    }

    override fun setRememberMe(rememberMe: Boolean) {
        mPreferencesHelper.setRememberMe(rememberMe)
    }

    override fun getRememberMe(): Boolean {
        return mPreferencesHelper.getRememberMe()
    }

    override fun saveUserData(mail: String, password: String, id: String) {
        mPreferencesHelper.saveUserData(mail,password, id)
    }

    override fun getUserData(): Triple<String?, String?, String?> {
        return mPreferencesHelper.getUserData()
    }

    override fun clearUserData() {
        mPreferencesHelper.clearUserData()
    }

    override fun getCurrentTheme(context: Context): String {
        return mPreferencesHelper.getCurrentTheme(context)
    }

    override fun setCurrentTheme(context: Context, currentTheme: String) {
        mPreferencesHelper.setCurrentTheme(context,currentTheme)
    }

}