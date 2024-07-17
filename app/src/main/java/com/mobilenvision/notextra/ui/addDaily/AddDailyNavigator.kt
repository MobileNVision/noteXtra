package com.mobilenvision.notextra.ui.addDaily

import com.mobilenvision.notextra.data.model.db.Daily


interface AddDailyNavigator {
    fun onSaveClick()
    fun onSuccessAddNote()
    fun onFailure(message: String?)
    fun onSuccessAddNoteToDatabase()
    fun onNoteMicrophoneClick()
    fun onAddImageClick()
    fun setDaily(day: Daily)
}