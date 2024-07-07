package com.mobilenvision.notextra.ui.base


import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import java.lang.ref.WeakReference

abstract class BaseViewModel<N>(var dataManager: DataManager) : ViewModel() {
    val isBaseLoading = ObservableBoolean()
    private var mNavigator: WeakReference<N>? = null


    fun setIsLoading(isLoading: Boolean) {
        isBaseLoading.set(isLoading)
    }

    val navigator: N?
        get() = mNavigator!!.get()

    fun setNavigator(navigator: N) {
        mNavigator = WeakReference(navigator)
    }

    fun setRememberMe(checked: Boolean) {
        dataManager.setRememberMe(checked)
    }

    fun getRememberMe(): Boolean {
        return dataManager.getRememberMe()
    }

    fun saveUserData(userMail: String, userPassword: String, id: String) {
        dataManager.saveUserData(userMail, userPassword, id)
    }

    fun getUserData(): Triple<String?, String?, String?> {
        return dataManager.getUserData()
    }

    fun clearUserData(){
        dataManager.clearUserData()
    }

    fun getAllNotes(dbCallback: DbCallback<List<Note>>) {
        dataManager.getAllNotes(dbCallback)
    }

    fun getUnSynchronized(dbCallback: DbCallback<List<Note>>) {
        dataManager.getUnSynchronized(dbCallback)
    }

    fun getCurrentTheme(context: Context): String{
    return dataManager.getCurrentTheme(context)
    }

    fun changeTheme(context: Context){
        val currentTheme = dataManager.getCurrentTheme(context)

        if (currentTheme == "LIGHT_THEME") {
            dataManager.setCurrentTheme(context ,"DARK_THEME")
        } else {
            dataManager.setCurrentTheme(context,"LIGHT_THEME")
        }
    }

    fun setCurrentTheme(context: Context, currentTheme: String){
            dataManager.setCurrentTheme(context ,currentTheme)

    }
}
