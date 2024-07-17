package com.mobilenvision.notextra.ui.daily

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
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.ui.base.BaseViewModel
import com.mobilenvision.notextra.utils.CommonUtils

class DailyViewModel (dataManager: DataManager) : BaseViewModel<DailyNavigator>(dataManager) {
    private var mInternetStatus: Boolean = false
    private var mDailyList: ArrayList<Daily> = ArrayList()
    private var noteList: MutableLiveData<List<Daily>> = MutableLiveData()
    private var isEmpty = ObservableBoolean()
    var categoryList: ArrayList<Category> = ArrayList()
    var categories: MutableLiveData<List<Category>> = MutableLiveData()
    val db = FirebaseFirestore.getInstance()

    fun getListFromDatabase() {
        isBaseLoading.set(true)
        dataManager.getAllDailyNotes(object : DbCallback<List<Daily>> {
            override fun onSuccess(result: List<Daily>) {
                isBaseLoading.set(false)
                if(result.isEmpty()){
                    isEmpty.set(true)
                } else{
                    isEmpty.set(false)
                }
                mDailyList.clear()
                mDailyList.addAll(result)
                navigator?.setDailyList(mDailyList)
            }

            override fun onError(error: Throwable) {
                isBaseLoading.set(false)
                navigator?.emptyDailyList()
                isEmpty.set(true)
            }
        })
    }

    fun getFontFamily(): String {
        return dataManager.getFont()
    }

    fun getDailyNotesFromFirebase(userId: String) {
        isBaseLoading.set(true)

        val notesCollection = Firebase.firestore.collection("daily")
        notesCollection.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { querySnapshot ->
                isBaseLoading.set(false)
                val notes = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Daily::class.java)
                }
                if (notes.isEmpty()) {
                    isEmpty.set(true)
                } else {
                    isEmpty.set(false)
                    mDailyList.clear()
                    mDailyList.addAll(notes)
                    navigator?.setDailyList(mDailyList)
                }
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
                navigator?.emptyDailyList()
                isEmpty.set(true)
                Handler(Looper.getMainLooper()).post {
                    navigator?.onFailure("Failed to retrieve notes from Firebase: ${e.message}")
                }
            }
    }

    fun getUnSynchronizedDailyNotes(){
        isBaseLoading.set(true)
        dataManager.getUnSynchronizedDaily(object : DbCallback<List<Daily>> {
            override fun onSuccess(result: List<Daily>) {
                if(result.isNotEmpty()){
                    mDailyList.clear()
                    mDailyList.addAll(result)
                    setUnSynchronizedDailyList(mDailyList)
                }
                isBaseLoading.set(false)
            }

            override fun onError(error: Throwable) {
                isBaseLoading.set(false)
            }
        })
    }

    private fun setUnSynchronizedDailyList(mDailyList: ArrayList<Daily>) {
        val notesCollection = db.collection("dailyNotes")
        val batch = db.batch()

        for (daily in mDailyList) {
            val noteRef = notesCollection.document(daily.id)
            batch.set(noteRef, CommonUtils.dailyToMap(daily))
        }

        batch.commit()
            .addOnSuccessListener {
                var remainingUpdates = mDailyList.size

                for (note in mDailyList) {
                    dataManager.updateIsSynchronized(note.id, true, object : DbCallback<Boolean> {
                        override fun onSuccess(result: Boolean) {
                            remainingUpdates--
                            if (remainingUpdates == 0) {
                                Handler(Looper.getMainLooper()).post {
                                    navigator?.onSuccessAddDailyNotes()
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

    fun addDailyList(mDailyList: ArrayList<Daily>){
        noteList.postValue(mDailyList)
    }
    fun getDailyList() : LiveData<List<Daily>> {
        return noteList
    }
    fun getIsEmpty(): ObservableBoolean{
        return isEmpty
    }
    fun addDailyButtonClick(){
        navigator?.addDailyButtonClick()
    }


    fun deleteDaily(note: Daily) {
        isBaseLoading.set(true)
        dataManager.deleteDaily(note,object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                if(mInternetStatus) {
                    deleteInFirebase(note.id, 1)
                }
                else{
                    dataManager.saveDeletedDailyId(note.id)
                    navigator?.deleteDailySuccessToDatabase()
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
                dataManager.removeDeletedDailyId(id)
                if (size == 1) {
                    navigator?.deleteDailySuccess()
                }
            }
            .addOnFailureListener { e ->
                navigator?.onFailure("Failed to delete note from Firestore: ${e.message}")
                isBaseLoading.set(false)
            }
    }

    fun setInternetStatus(internetStatus: Boolean) {
        mInternetStatus = internetStatus
    }

    fun syncDeletedDailyNotes() {
        val deletedDailyIds = dataManager.getDeletedDailyIds()
        if (!deletedDailyIds.isNullOrEmpty()) {
            var size = deletedDailyIds.size
            for (noteId in deletedDailyIds) {
                deleteInFirebase(noteId, size)
                size--
            }
        }
    }
    fun getDayClick(){
        navigator?.getDayClick()
    }
}