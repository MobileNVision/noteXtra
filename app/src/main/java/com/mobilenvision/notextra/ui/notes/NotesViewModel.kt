package com.mobilenvision.notextra.ui.notes

import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel
import com.mobilenvision.notextra.utils.CommonUtils

class NotesViewModel (dataManager: DataManager) : BaseViewModel<NotesNavigator>(dataManager) {
    private var mInternetStatus: Boolean = false
    private var mNoteList: ArrayList<Note> = ArrayList()
    private var noteList: MutableLiveData<List<Note>> = MutableLiveData()
    private var isEmpty = ObservableBoolean()
    var categoryList: ArrayList<Category> = ArrayList()
    var categories: MutableLiveData<List<Category>> = MutableLiveData()
    val db = FirebaseFirestore.getInstance()

    fun getListFromDatabase() {
        isBaseLoading.set(true)
        dataManager.getAllNotes(object : DbCallback<List<Note>> {
            override fun onSuccess(result: List<Note>) {
                isBaseLoading.set(false)
                if(result.isEmpty()){
                    isEmpty.set(true)
                } else{
                    isEmpty.set(false)
                }
                mNoteList.clear()
                mNoteList.addAll(result)
                navigator?.setNoteList(mNoteList)
            }

            override fun onError(error: Throwable) {
                isBaseLoading.set(false)
                navigator?.emptyNoteList()
                isEmpty.set(true)
            }
        })
    }

    fun getNotesFromFirebase(userId: String) {
        isBaseLoading.set(true)

        val notesCollection = Firebase.firestore.collection("notes")
        notesCollection.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                isBaseLoading.set(false)
                val notes = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Note::class.java)
                }
                if (notes.isEmpty()) {
                    isEmpty.set(true)
                } else {
                    isEmpty.set(false)
                    mNoteList.clear()
                    mNoteList.addAll(notes)
                    navigator?.setNoteList(mNoteList)
                }
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
                navigator?.emptyNoteList()
                isEmpty.set(true)
                Handler(Looper.getMainLooper()).post {
                    navigator?.onFailure("Failed to retrieve notes from Firebase: ${e.message}")
                }
            }
    }

    fun getUnSynchronizedNotes(){
        isBaseLoading.set(true)
        dataManager.getUnSynchronized(object : DbCallback<List<Note>> {
            override fun onSuccess(result: List<Note>) {
                if(result.isNotEmpty()){
                    mNoteList.clear()
                    mNoteList.addAll(result)
                    setUnSynchronizedNoteList(mNoteList)
                }
                isBaseLoading.set(false)
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
            val noteRef = notesCollection.document(note.id)
            batch.set(noteRef, CommonUtils.noteToMap(note))
        }

        batch.commit()
            .addOnSuccessListener {
                var remainingUpdates = mNoteList.size

                for (note in mNoteList) {
                    dataManager.updateIsSynchronized(note.id, true, object : DbCallback<Boolean> {
                        override fun onSuccess(result: Boolean) {
                            remainingUpdates--
                            if (remainingUpdates == 0) {
                                Handler(Looper.getMainLooper()).post {
                                    navigator?.onSuccessAddNotes()
                                }
                            }
                        }

                        override fun onError(error: Throwable) {
                            remainingUpdates--
                            if (remainingUpdates == 0) {
                                Handler(Looper.getMainLooper()).post {
                                    navigator?.onFailure(error.message)
                                }
                            }
                        }
                    })
                }
            }
            .addOnFailureListener { e ->
                navigator?.onFailure(e.message)
            }
    }

    fun getCategory(){
        dataManager.getAllCategories(object : DbCallback<List<Category>> {
            override fun onSuccess(result: List<Category>) {
                categoryList.clear()
                categoryList.addAll(result)
                categories.postValue(categoryList)
                Handler(Looper.getMainLooper()).post {
                    navigator?.setCategoryList(categoryList)
                }
            }

            override fun onError(error: Throwable) {
            }
        })
    }
    fun getCategories(): List<Category>? {
        return categories.value
    }
    fun addNoteList(mNoteList: ArrayList<Note>){
        noteList.postValue(mNoteList)
    }
    fun getNoteList() : LiveData<List<Note>> {
        return noteList
    }
    fun getIsEmpty(): ObservableBoolean{
        return isEmpty
    }
    fun addNoteButtonClick(){
        navigator?.addNoteButtonClick()
    }

    fun updateCategory(id: String, category: String) {
        isBaseLoading.set(true)
        dataManager.updateCategoryOfNote(id,category,object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                updateCategoryInFirebase(id,category)
                isBaseLoading.set(false)

            }

            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
                isBaseLoading.set(false)
            }
        })
    }
    fun updateCategoryInFirebase(id: String, category: String) {
        isBaseLoading.set(true)

        val notesCollection = Firebase.firestore.collection("notes")
        val noteDocument = notesCollection.document(id)

        noteDocument.update("category", category)
            .addOnSuccessListener {
                isBaseLoading.set(false)
                navigator?.addCategorySuccess()
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
                navigator?.onFailure(e.message)
            }
    }
    fun deleteNote(note: Note) {
        isBaseLoading.set(true)
        dataManager.deleteNote(note,object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                if(mInternetStatus) {
                    deleteInFirebase(note.id, 1)
                }
                else{
                    dataManager.saveDeletedNoteId(note.id)
                    navigator?.deleteNoteSuccessToDatabase()
                }

            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
                isBaseLoading.set(false)
            }
        })
    }

    private fun deleteInFirebase(id: String, size: Int) {
        val notesCollection = Firebase.firestore.collection("notes")
        val noteDocument = notesCollection.document(id)

        noteDocument.delete()
            .addOnSuccessListener {
                deleteNoteVersions(id)
                if (size == 1) {
                    navigator?.deleteNoteSuccess()
                }
            }
            .addOnFailureListener { e ->
                navigator?.onFailure("Failed to delete note from Firestore: ${e.message}")
                isBaseLoading.set(false)
            }
    }

    private fun deleteNoteVersions(id: String) {
        val versionsCollection = Firebase.firestore
            .collection("notes")
            .document(id)
            .collection("versions")

        versionsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val batch = Firebase.firestore.batch()
                querySnapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        navigator?.deleteNoteSuccess()
                        isBaseLoading.set(false)
                    }
                    .addOnFailureListener { e ->
                        navigator?.onFailure("Failed to delete note versions from Firestore: ${e.message}")
                        isBaseLoading.set(false)
                    }
                    .addOnCompleteListener {
                        isBaseLoading.set(false)
                    }
            }
            .addOnFailureListener { e ->
                navigator?.onFailure("Failed to fetch note versions for deletion: ${e.message}")
                isBaseLoading.set(false)
            }
    }

    fun onFilterClick(){
        navigator?.onFilterClick()
    }

    fun setInternetStatus(internetStatus: Boolean) {
        mInternetStatus = internetStatus
    }

    fun syncDeletedNotes() {
        val deletedNoteIds = dataManager.getDeletedNoteIds()
        if (!deletedNoteIds.isNullOrEmpty()) {
            var size = deletedNoteIds.size
            for (noteId in deletedNoteIds) {
                deleteInFirebase(noteId, size)
                size--
            }
        }
    }

}