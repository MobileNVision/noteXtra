package com.mobilenvision.notextra.ui.notes

import com.mobilenvision.notextra.data.model.db.Note

interface NotesNavigator {
    fun addNoteButtonClick()
    fun emptyNoteList()
    fun setNoteList(mNoteList: ArrayList<Note>)
    fun addCategorySuccess()
    fun onFailure(message: String?)
    fun deleteNoteSuccess()
}