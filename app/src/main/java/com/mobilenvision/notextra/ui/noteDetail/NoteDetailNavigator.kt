package com.mobilenvision.notextra.ui.noteDetail

import com.mobilenvision.notextra.data.model.db.Category

interface NoteDetailNavigator {
    fun onFailure(message: String?)
    fun setCategoryList(categoryList: ArrayList<Category>)
    fun onEditClick()
    fun onDeleteClick()
    fun addReminderClick()
    fun onDeleteCategoryClick()
    fun onAddCategoryClick()
    fun onEditCategoryClick()
    fun onSuccessUpdateNote()
    fun deleteNoteSuccess()
    fun onSuccessAddCategory()
    fun onSuccessUpdateCategory()
    fun onSuccessDeleteCategory()
}