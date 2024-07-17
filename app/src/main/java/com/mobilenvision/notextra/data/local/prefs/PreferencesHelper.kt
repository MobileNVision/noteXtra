package com.mobilenvision.notextra.data.local.prefs

import android.content.Context


interface PreferencesHelper {
    fun setRememberMe(rememberMe: Boolean)
    fun getRememberMe(): Boolean
    fun saveUserData(mail: String, password: String, id: String)
    fun getUserData(): Triple<String?, String?, String?>
    fun clearUserData()
    fun getCurrentTheme(context: Context): String
    fun setCurrentTheme(context: Context, currentTheme: String)
    fun getFont(): String
    fun setFont(font: String)
    fun getDeletedNoteIds(): MutableSet<String>?
    fun saveDeletedNoteId(noteId: String)
    fun removeDeletedNoteId(noteId: String)
    fun saveDeletedDailyId(id: String)
    fun removeDeletedDailyId(id: String)
    fun getDeletedDailyIds(): MutableSet<String>?
}