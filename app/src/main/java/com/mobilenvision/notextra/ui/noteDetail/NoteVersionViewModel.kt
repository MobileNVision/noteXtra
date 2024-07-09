package com.mobilenvision.notextra.ui.noteDetail

import androidx.lifecycle.MutableLiveData
import com.mobilenvision.notextra.data.model.db.Note

class NoteVersionViewModel() {
    private var noteLiveData: MutableLiveData<Note> = MutableLiveData()
    private lateinit var noteItemViewModelListener: NoteItemViewModelListener

    constructor(note: Note, noteItemViewModelListener: NoteItemViewModelListener) : this() {
        this.noteLiveData.value = note
        this.noteItemViewModelListener = noteItemViewModelListener
    }

    fun onItemClick() {
        noteItemViewModelListener.onNoteItemClick(noteLiveData.value!!)
    }
    fun getNoteData(): Note? {
        return noteLiveData.value
    }
    fun getFormattedVersion(): String {
        return "v." + (noteLiveData.value?.version?.toString() ?: "")
    }
    fun getFormattedPriority(): String {
        return (noteLiveData.value?.priority ?: "")
    }

    interface NoteItemViewModelListener {
        fun onNoteItemClick(note: Note)
    }
}