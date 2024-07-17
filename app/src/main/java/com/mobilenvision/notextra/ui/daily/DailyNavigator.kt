package com.mobilenvision.notextra.ui.daily

import com.mobilenvision.notextra.data.model.db.Daily

interface DailyNavigator {
    fun addDailyButtonClick()
    fun emptyDailyList()
    fun setDailyList(mDailyList: ArrayList<Daily>)
    fun onFailure(message: String?)
    fun deleteDailySuccess()
    fun onSuccessAddDailyNotes()
    fun deleteDailySuccessToDatabase()
    fun getDayClick()

}