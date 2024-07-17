package com.mobilenvision.notextra.ui.daily

import androidx.lifecycle.MutableLiveData
import com.mobilenvision.notextra.data.model.db.Daily

class DailyItemViewModel() {
    private var dailyLiveData: MutableLiveData<Daily> = MutableLiveData()
    private lateinit var dailyItemViewModelListener: DailyItemViewModelListener
    private var font: MutableLiveData<String> = MutableLiveData()

    constructor(daily: Daily, dailyItemViewModelListener: DailyItemViewModelListener) : this() {
        this.dailyLiveData.value = daily
        this.dailyItemViewModelListener = dailyItemViewModelListener
    }

    fun onItemClick() {
        dailyItemViewModelListener.onDailyItemClick(dailyLiveData.value!!)
    }

    fun getNote(): Daily? {
        return dailyLiveData.value
    }

    fun setFont(mFont: String) {
        font.value = mFont
    }

    fun getFont() : MutableLiveData<String>{
        return font
    }

    interface DailyItemViewModelListener {
        fun onDailyItemClick(daily: Daily)
    }
}