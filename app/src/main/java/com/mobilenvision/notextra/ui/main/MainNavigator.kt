package com.mobilenvision.notextra.ui.main

import com.mobilenvision.notextra.data.model.db.Note

interface MainNavigator {
    fun onSuccess(message: String)
    fun onError(message: String)
    fun onLogoutSuccess()
    fun onFailure(message: String?)
    fun setNote(result: Note)
    fun onMicrophoneClick()
}