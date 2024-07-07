package com.mobilenvision.notextra.ui.main

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel
import com.mobilenvision.notextra.utils.CommonUtils

class MainViewModel(dataManager: DataManager) : BaseViewModel<MainNavigator>(dataManager) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mNoteList: ArrayList<Note> = ArrayList()
    val db = FirebaseFirestore.getInstance()

    fun onLogoutClick(){
        setIsLoading(true)
        auth.signOut()
        dataManager.setRememberMe(false)
        dataManager.clearUserData()
        navigator?.onLogoutSuccess()
        setIsLoading(false)
    }

    fun getUnSynchronizedNotes(){
        isBaseLoading.set(true)
        dataManager?.getUnSynchronized(object : DbCallback<List<Note>> {
            override fun onSuccess(result: List<Note>) {
                isBaseLoading.set(false)
                mNoteList.clear()
                mNoteList.addAll(result)
                setUnSynchronizedNoteList(mNoteList)
            }
            override fun onError(error: Throwable) {
                isBaseLoading.set(false)
            }
        })
    }

    private fun setUnSynchronizedNoteList(mNoteList: ArrayList<Note>) {
        val notesCollection = db.collection("notes")
        val batch = db.batch()

        for (note in mNoteList) {
            val noteRef = notesCollection.document(note.id.toString())
            batch.set(noteRef, CommonUtils.noteToMap(note))
        }

        batch.commit()
            .addOnSuccessListener {
                navigator?.onSuccessAddNotes()
            }
            .addOnFailureListener { e ->
                navigator?.onFailure(e.message)
            }
    }

    fun getNote(noteId: String) {
        dataManager.loadNoteById(noteId, object : DbCallback<Note> {
            override fun onSuccess(result: Note) {
                navigator?.setNote(result)
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }

}
