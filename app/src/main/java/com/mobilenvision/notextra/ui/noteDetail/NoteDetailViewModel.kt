package com.mobilenvision.notextra.ui.noteDetail

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel
import com.mobilenvision.notextra.utils.CommonUtils

class NoteDetailViewModel (dataManager: DataManager) : BaseViewModel<NoteDetailNavigator>(dataManager) {
    private var mInternetStatus: Boolean = false
    var categoryList: ArrayList<Category> = ArrayList()
    private var categories: MutableLiveData<List<Category>> = MutableLiveData()
    fun onEditClick(){
        navigator?.onEditClick()
    }
    fun onDeleteClick(){
        navigator?.onDeleteClick()
    }
    fun onHistoryClick(){
        navigator?.onHistoryClick()
    }
    fun onAddReminderClick(){
        navigator?.addReminderClick()
    }
    fun onDeleteCategoryClick(){
        navigator?.onDeleteCategoryClick()
    }
    fun onAddCategoryClick(){
        navigator?.onAddCategoryClick()
    }
    fun onEditCategoryClick(){
        navigator?.onEditCategoryClick()
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
                navigator?.onFailure(error.message)
            }
        })
    }

    fun getCategories(): LiveData<List<Category>> {
        return categories
    }

    fun updateNote(note: Note){
        isBaseLoading.set(true)
        dataManager.updateNote(note , object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                Handler(Looper.getMainLooper()).post {
                    isBaseLoading.set(false)
                    saveNoteVersion(note)
                }
            }
            override fun onError(error: Throwable) {
                isBaseLoading.set(false)
                navigator?.onFailure(error.message)
            }
        })
    }
    fun saveNoteVersion(note: Note) {
        isBaseLoading.set(true)
        val notesCollection = Firebase.firestore.collection("notes")
        val noteDocument = notesCollection.document(note.id)

        noteDocument.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val currentNoteData = documentSnapshot.data
                val versionsCollection = noteDocument.collection("versions")

                val newVersionId = versionsCollection.document().id
                versionsCollection.document(newVersionId)
                    .set(currentNoteData!!)
                    .addOnFailureListener { e ->
                        Handler(Looper.getMainLooper()).post {
                            isBaseLoading.set(false)
                            navigator?.onFailure("Failed to save current note version in Firebase: ${e.message}")
                        }
                    }
                isBaseLoading.set(true)
                updateNoteInFirebase(note)
            }
        }.addOnFailureListener { e ->
            Handler(Looper.getMainLooper()).post {
                isBaseLoading.set(true)
                navigator?.onFailure("Failed to retrieve current note from Firebase: ${e.message}")
            }
        }
    }

    private fun updateNoteInFirebase(note: Note) {
        isBaseLoading.set(true)
        val notesCollection = Firebase.firestore.collection("notes")
        val noteDocument = notesCollection.document(note.id)

        noteDocument.set(CommonUtils.noteToMap(note))
            .addOnSuccessListener {
                Handler(Looper.getMainLooper()).post {
                    isBaseLoading.set(false)
                    navigator?.onSuccessUpdateNote(note)
                }
            }
            .addOnFailureListener { e ->
                Handler(Looper.getMainLooper()).post {
                    isBaseLoading.set(false)
                    navigator?.onFailure("Failed to update note in Firebase: ${e.message}")
                }
            }
    }

    fun deleteNote(note: Note) {
        isBaseLoading.set(true)
        dataManager.deleteNote(note,object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                if (mInternetStatus) {
                    deleteInFirebase(note.id)
                }
                else{
                    dataManager.saveDeletedNoteId(note.id)
                    navigator?.deleteNoteSuccess()
                }
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
                isBaseLoading.set(false)
            }
        })
    }

    private fun deleteInFirebase(id: String) {
        val notesCollection = Firebase.firestore.collection("notes")
        val noteDocument = notesCollection.document(id)

        noteDocument.delete()
            .addOnSuccessListener {
                deleteNoteVersions(id)
                isBaseLoading.set(false)
                navigator?.deleteNoteSuccess()
            }
            .addOnFailureListener { e ->
                navigator?.onFailure("Failed to delete note from Firestore: ${e.message}")
                isBaseLoading.set(false)
            }
    }

    private fun deleteNoteVersions(id: String) {
        isBaseLoading.set(true)
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

    fun insertCategory(categoryName: String) {
        val category = Category(name = categoryName)
        dataManager.insertCategory(category, object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                navigator?.onSuccessAddCategory()
                getCategory()
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }

    fun updateCategory(newCategoryName: String, exCategoryName: String){
        dataManager.updateCategory(newCategoryName, exCategoryName , object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                navigator?.onSuccessUpdateCategory()
                getCategory()
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }

    fun deleteCategoryByName(categoryName: String) {
        dataManager.deleteCategoryByName(categoryName, object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                navigator?.onSuccessDeleteCategory()
                getCategory()
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }
    fun fetchNoteVersions(noteId: String) {
        val notesCollection = Firebase.firestore.collection("notes")
        val noteDocument = notesCollection.document(noteId)
        val versionsCollection = noteDocument.collection("versions")

        versionsCollection.orderBy("version")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val noteVersions = ArrayList<Note>()

                for (document in querySnapshot.documents) {
                    val note = document.toObject(Note::class.java)
                    if (note != null) {
                        noteVersions.add(note)
                    }
                }
                navigator?.handleNoteVersions(noteVersions)
            }
            .addOnFailureListener { e ->
                Handler(Looper.getMainLooper()).post {
                    navigator?.onFailure("Failed to retrieve note versions from Firebase: ${e.message}")
                }
            }
    }

    fun setInternetStatus(internetStatus: Boolean) {
        mInternetStatus = internetStatus
    }

}