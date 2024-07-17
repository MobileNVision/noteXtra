package com.mobilenvision.notextra.data


import android.content.Context
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.local.db.DbHelper
import com.mobilenvision.notextra.data.local.prefs.PreferencesHelper
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.data.model.db.User
import javax.inject.Inject

class AppDataManager @Inject constructor(
    dbHelper: DbHelper,
    preferencesHelper: PreferencesHelper,
) : DataManager {

    private var mPreferencesHelper: PreferencesHelper = preferencesHelper
    private var mDbHelper: DbHelper = dbHelper


    override fun getAllUsers(callback: DbCallback<List<User?>>) {
        mDbHelper.getAllUsers(callback)
    }

    override fun getAllNotes(callback: DbCallback<List<Note>>) {
        mDbHelper.getAllNotes(callback)
    }

    override fun getAllDaily(callback: DbCallback<List<Daily>>) {
        mDbHelper.getAllDaily(callback)
    }

    override fun getUnSynchronized(callback: DbCallback<List<Note>>) {
        mDbHelper.getUnSynchronized(callback)
    }

    override fun getUnSynchronizedDaily(callback: DbCallback<List<Daily>>) {
        mDbHelper.getUnSynchronizedDaily(callback)
    }


    override fun insertUser(user: User, callback: DbCallback<Boolean>) {
        mDbHelper.insertUser(user, callback)
    }

    override fun loadNoteById(noteIds: String, callback: DbCallback<Note>) {
        mDbHelper.loadNoteById(noteIds, callback)
    }

    override fun loadDailyById(dailyIds: String, callback: DbCallback<Daily>) {
        mDbHelper.loadDailyById(dailyIds,callback)
    }

    override fun loadDailyByDay(day: String, callback: DbCallback<Daily>) {
        mDbHelper.loadDailyByDay(day,callback)
    }

    override fun insertNote(note: Note, callback: DbCallback<Boolean>) {
        mDbHelper.insertNote(note, callback)
    }

    override fun insertDaily(daily: Daily, callback: DbCallback<Boolean>) {
        mDbHelper.insertDaily(daily,callback)
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

    override fun insertCategory(categoryName: Category, callback: DbCallback<Boolean>) {
        mDbHelper.insertCategory(categoryName,callback)
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

    override fun updateIsSynchronizedDaily(
        dailyId: String,
        isSynchronized: Boolean,
        callback: DbCallback<Boolean>
    ) {
        mDbHelper.updateIsSynchronizedDaily(dailyId,isSynchronized,callback)
    }

    override fun loadUserById(userId: String, callback: DbCallback<User>) {
        mDbHelper.loadUserById(userId, callback)
    }

    override fun updateUser(user: User, callback: DbCallback<Boolean>) {
        mDbHelper.updateUser(user, callback)
    }

    override fun getAllDailyNotes(dbCallback: DbCallback<List<Daily>>) {
        mDbHelper.getAllDailyNotes(dbCallback)
    }

    override fun deleteDaily(daily: Daily, dbCallback: DbCallback<Boolean>) {
        mDbHelper.deleteDaily(daily, dbCallback)
    }

    override fun checkDailyExists(id: String, dbCallback: DbCallback<Boolean>) {
        mDbHelper.checkDailyExists(id,dbCallback)
    }

    override fun deleteNote(note: Note?, callback: DbCallback<Boolean>) {
        mDbHelper.deleteNote(note, callback)
    }

    override fun updateNote(note: Note, callback: DbCallback<Boolean>) {
        mDbHelper.updateNote(note, callback)
    }

    override fun updateDaily(daily: Daily, callback: DbCallback<Boolean>) {
        mDbHelper.updateDaily(daily,callback)
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

    override fun getFont(): String {
        return mPreferencesHelper.getFont()
    }

    override fun setFont(font: String) {
        mPreferencesHelper.setFont(font)
    }

    override fun getDeletedNoteIds(): MutableSet<String>? {
        return mPreferencesHelper.getDeletedNoteIds()
    }

    override fun saveDeletedNoteId(noteId: String) {
        mPreferencesHelper.saveDeletedNoteId(noteId)
    }

    override fun removeDeletedNoteId(noteId: String) {
        mPreferencesHelper.removeDeletedNoteId(noteId)
    }

    override fun saveDeletedDailyId(id: String) {
        mPreferencesHelper.saveDeletedDailyId(id)
    }

    override fun removeDeletedDailyId(id: String) {
        mPreferencesHelper.removeDeletedDailyId(id)
    }

    override fun getDeletedDailyIds(): MutableSet<String>? {
        return mPreferencesHelper.getDeletedDailyIds()
    }

}