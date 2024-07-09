package com.mobilenvision.notextra.ui.notes

import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note

interface NotesNavigator {
    fun addNoteButtonClick()
    fun emptyNoteList()
    fun setNoteList(mNoteList: ArrayList<Note>)
    fun addCategorySuccess()
    fun onFailure(message: String?)
    fun deleteNoteSuccess()
    fun onFilterClick()
    fun setCategoryList(categoryList: ArrayList<Category>)
    fun onSuccessAddNotes()
    fun deleteNoteSuccessToDatabase()

}