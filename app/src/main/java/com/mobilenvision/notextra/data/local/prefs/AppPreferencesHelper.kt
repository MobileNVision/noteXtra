package com.mobilenvision.notextra.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import com.mobilenvision.notextra.di.PreferenceInfo
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(
    context: Context,
    @PreferenceInfo prefFileName: String?
) : PreferencesHelper {

    private var sharedPreferences: SharedPreferences? = null

    companion object {
        private const val KEY_REMEMBER_ME = "rememberMe"
        private const val KEY_USER_MAIL = "userMail"
        private const val KEY_USER_PASSWORD = "userPassword"
        private const val KEY_USER_ID = "userID"
        private const val DELETED_NOTES = "deletedNotes"
        private const val UDKEY_CACHED_BRS_MOBILE_THEME = "UDKEY_CACHED_BRS_MOBILE_THEME"

    }

    override fun getCurrentTheme(context: Context): String {
        return sharedPreferences!!.getString(UDKEY_CACHED_BRS_MOBILE_THEME, "DARK_THEME") ?: "DARK_THEME"
    }

    override fun setCurrentTheme(context: Context, currentTheme: String) {
        sharedPreferences!!.edit().putString(UDKEY_CACHED_BRS_MOBILE_THEME, currentTheme).apply()
    }

    init {
        sharedPreferences = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    }
    override fun setRememberMe(rememberMe: Boolean) {
        sharedPreferences!!.edit().putBoolean(KEY_REMEMBER_ME, rememberMe).apply()
    }

    override fun getRememberMe(): Boolean {
        return sharedPreferences!!.getBoolean(KEY_REMEMBER_ME, false)
    }

    override fun saveUserData(mail: String, password: String, id: String) {
        sharedPreferences!!.edit()
            .putString(KEY_USER_MAIL, mail)
            .putString(KEY_USER_PASSWORD, password)
            .putString(KEY_USER_ID, id)
            .apply()
    }

    override fun getUserData(): Triple<String?, String?, String?> {
        val mail = sharedPreferences!!.getString(KEY_USER_MAIL, null)
        val password = sharedPreferences!!.getString(KEY_USER_PASSWORD, null)
        val id = sharedPreferences!!.getString(KEY_USER_ID, null)
        return Triple(mail, password,id)
    }



    override fun clearUserData() {
        sharedPreferences!!.edit()
            .remove(KEY_USER_MAIL)
            .remove(KEY_USER_PASSWORD)
            .remove(KEY_USER_ID)
            .apply()
    }
    override fun saveDeletedNoteId(noteId: String) {
        val editor = sharedPreferences!!.edit()
        val deletedNotes = sharedPreferences!!.getStringSet(DELETED_NOTES, HashSet())
        deletedNotes?.add(noteId)
        editor.putStringSet(DELETED_NOTES, deletedNotes)
        editor.apply()
    }

    override fun getDeletedNoteIds(): MutableSet<String>? {
        return sharedPreferences!!.getStringSet(DELETED_NOTES, HashSet())
    }

    override fun removeDeletedNoteId(noteId: String) {
        val editor = sharedPreferences!!.edit()
        val deletedNotes = sharedPreferences!!.getStringSet(DELETED_NOTES, HashSet())
        deletedNotes?.remove(noteId)
        editor.putStringSet(DELETED_NOTES, deletedNotes)
        editor.apply()
    }

}