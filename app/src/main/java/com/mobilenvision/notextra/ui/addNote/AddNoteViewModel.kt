package com.mobilenvision.notextra.ui.addNote

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel
import com.mobilenvision.notextra.utils.CommonUtils

class AddNoteViewModel (dataManager: DataManager) : BaseViewModel<AddNoteNavigator>(dataManager) {
    val db = FirebaseFirestore.getInstance()
    var categoryList: ArrayList<Category> = ArrayList()
    var isInternetAvailable: Boolean = false
    private var categories: MutableLiveData<List<Category>> = MutableLiveData()

    fun onSaveClick(){
        navigator?.onSaveClick()
    }
    fun onAddReminderClick(){
        navigator?.onAddReminderClick()
    }

    fun onAddCategoryClick(){
        navigator?.onAddCategoryClick()
    }

    fun onEditCategoryClick(){
        navigator?.onEditCategoryClick()
    }

    fun onDeleteCategoryClick(){
        navigator?.onDeleteCategoryClick()
    }
    fun getCategory(){
        dataManager?.getAllCategories(object : DbCallback<List<Category>> {
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

    fun insertNote(note: Note) {
            dataManager.insertNote(note, object : DbCallback<Boolean> {
                override fun onSuccess(result: Boolean) {
                    if(isInternetAvailable) {
                        addNoteToFirestore(note)
                    }
                    else{
                        navigator?.onSuccessAddNoteToDatabase()
                    }
                }
                override fun onError(error: Throwable) {
                    navigator?.onFailure(error.message)
                }
            })

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

    fun deleteCategoryByName(categoryName: String){
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

    fun addNoteToFirestore(note: Note) {
        val notesCollection = db.collection("notes")
        notesCollection.document(note.id)
            .set(CommonUtils.noteToMap(note))
            .addOnSuccessListener {
                navigator?.onSuccessAddNote()
            }
            .addOnFailureListener { e ->
                navigator?.onFailure(e.message)
            }
}

    fun setIsInternetAvailable(internetAvailability: Boolean) {
        isInternetAvailable = internetAvailability
    }

}