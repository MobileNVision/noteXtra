package com.mobilenvision.notextra.ui.addDaily

import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel
import com.mobilenvision.notextra.utils.CommonUtils

class AddDailyViewModel (dataManager: DataManager) : BaseViewModel<AddDailyNavigator>(dataManager) {
    val db = FirebaseFirestore.getInstance()
    var isInternetAvailable: Boolean = false
    private val font = MutableLiveData<String>()

    fun onSaveClick(){
        navigator?.onSaveClick()
    }
    fun getFont(): MutableLiveData<String>{
        return font
    }
    fun getFontFamily(){
        font.value = dataManager.getFont()
    }
    fun insertDaily(daily: Daily) {
        dataManager.checkDailyExists(daily.day!!, object : DbCallback<Boolean> {
            override fun onSuccess(exists: Boolean) {
                if (exists) {
                    dataManager.updateDaily(daily, object : DbCallback<Boolean> {
                        override fun onSuccess(result: Boolean) {
                            if (isInternetAvailable) {
                                addDailyToFirestore(daily)
                            } else {
                                navigator?.onSuccessAddNoteToDatabase()
                            }
                        }

                        override fun onError(error: Throwable) {
                            navigator?.onFailure(error.message)
                        }
                    })
                } else {
                  dataManager.insertDaily(daily, object : DbCallback<Boolean> {
                        override fun onSuccess(result: Boolean) {
                            if (isInternetAvailable) {
                                addDailyToFirestore(daily)
                            } else {
                                navigator?.onSuccessAddNoteToDatabase()
                            }
                        }

                        override fun onError(error: Throwable) {
                            navigator?.onFailure(error.message)
                        }
                    })
                }
            }

            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }

    fun addDailyToFirestore(daily: Daily) {
        val notesCollection = db.collection("daily")
        notesCollection.document(daily.id)
            .set(CommonUtils.dailyToMap(daily))
            .addOnSuccessListener {
                updateIsSynchronized(daily.id)
            }
            .addOnFailureListener { e ->
                navigator?.onFailure(e.message)
            }
    }


    private fun updateIsSynchronized(id: String) {
        dataManager.updateIsSynchronized(id,true, object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                Handler(Looper.getMainLooper()).post {
                    navigator?.onSuccessAddNote()
                }
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }

    fun setIsInternetAvailable(internetAvailability: Boolean) {
        isInternetAvailable = internetAvailability
    }


    fun onNoteMicrophoneClick(){
        navigator?.onNoteMicrophoneClick()
    }
    fun onAddImageClick(){
        navigator?.onAddImageClick()
    }

    fun loadFromFirebase(userId: String, day: String) {
        isBaseLoading.set(true)

        val notesCollection = Firebase.firestore.collection("daily")
        notesCollection.whereEqualTo("userId", userId)
            .whereEqualTo("day",day).get()
            .addOnSuccessListener { querySnapshot ->
                isBaseLoading.set(false)
                if (!querySnapshot.isEmpty) {
                    val daily = querySnapshot.documents[0].toObject(Daily::class.java)
                    navigator?.setDaily(daily!!)
                }
            }
            .addOnFailureListener { e ->
                isBaseLoading.set(false)
            }
    }

    fun loadFromDatabase(day: String) {
        isBaseLoading.set(true)
        dataManager.loadDailyByDay(day,object : DbCallback<Daily> {
            override fun onSuccess(result: Daily) {
                isBaseLoading.set(false)
                navigator?.setDaily(result)
            }

            override fun onError(error: Throwable) {
                isBaseLoading.set(false)
            }
        })
    }

}