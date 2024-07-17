package com.mobilenvision.notextra.ui.main

import com.google.firebase.auth.FirebaseAuth
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel

class MainViewModel(dataManager: DataManager) : BaseViewModel<MainNavigator>(dataManager) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun onLogoutClick(){
        setIsLoading(true)
        auth.signOut()
        dataManager.setRememberMe(false)
        dataManager.clearUserData()
        navigator?.onLogoutSuccess()
        setIsLoading(false)
    }

    fun onMicrophoneClick(){
        navigator?.onMicrophoneClick()
    }

    fun getNote(noteId: String) {
        dataManager.loadNoteById(noteId, object : DbCallback<Note> {
            override fun onSuccess(result: Note) {
                navigator?.setNote(result)
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }

    fun onBackClick(){
        navigator?.onBackClick()
    }
}
