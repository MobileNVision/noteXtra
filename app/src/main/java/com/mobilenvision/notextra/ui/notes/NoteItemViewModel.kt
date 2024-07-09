package com.mobilenvision.notextra.ui.notes

import android.view.View
import com.mobilenvision.notextra.data.model.db.Note
import androidx.lifecycle.MutableLiveData

class NoteItemViewModel() {
    private var noteLiveData: MutableLiveData<Note> = MutableLiveData()
    private lateinit var noteItemViewModelListener: NoteItemViewModelListener

    constructor(note: Note, noteItemViewModelListener: NoteItemViewModelListener) : this() {
        this.noteLiveData.value = note
        this.noteItemViewModelListener = noteItemViewModelListener
    }

    fun onItemClick() {
        noteItemViewModelListener.onNoteItemClick(noteLiveData.value!!)
    }
    fun onItemCategoryClick() {
        noteItemViewModelListener.onItemCategoryClick(noteLiveData.value!!)
    }
    fun onItemLongClick(view: View): Boolean {
        noteItemViewModelListener.onNoteItemLongClick(view, noteLiveData.value!!)
        return true
    }
    val onLongClickListener = View.OnLongClickListener { view ->
        onItemLongClick(view)
    }

    fun getNote(): Note? {
        return noteLiveData.value
    }


    interface NoteItemViewModelListener {
        fun onNoteItemClick(note: Note)
        fun onNoteItemLongClick(view: View, note: Note)
        fun onItemCategoryClick(note: Note)
    }
}