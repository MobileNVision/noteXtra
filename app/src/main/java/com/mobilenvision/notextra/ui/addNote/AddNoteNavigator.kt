package com.mobilenvision.notextra.ui.addNote

import com.mobilenvision.notextra.data.model.db.Category

interface AddNoteNavigator {
    fun setCategoryList(categoryList: ArrayList<Category>)
    fun onSaveClick()
    fun onSuccessAddNote()
    fun onFailure(message: String?)
    fun onAddCategoryClick()
    fun onSuccessAddCategory()
    fun onAddReminderClick()
    fun onEditCategoryClick()
    fun onSuccessUpdateCategory()
    fun onDeleteCategoryClick()
    fun onSuccessDeleteCategory()
    fun onSuccessAddNoteToDatabase()
    fun onTitleMicrophoneClick()
    fun onNoteMicrophoneClick()
}