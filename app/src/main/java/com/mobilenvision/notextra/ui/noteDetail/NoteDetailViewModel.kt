package com.mobilenvision.notextra.ui.noteDetail

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel

class NoteDetailViewModel (dataManager: DataManager) : BaseViewModel<NoteDetailNavigator>(dataManager) {
    var categoryList: ArrayList<Category> = ArrayList()
    private var categories: MutableLiveData<List<Category>> = MutableLiveData()
    fun onEditClick(){
        navigator?.onEditClick()
    }
    fun onDeleteClick(){
        navigator?.onDeleteClick()
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

    fun updateNote(note: Note){
        dataManager.updateNote(note , object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                navigator?.onSuccessUpdateNote()
                getCategory()
            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
            }
        })
    }

    fun deleteNote(note: Note) {
        isBaseLoading.set(true)
        dataManager.deleteNote(note,object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                navigator?.deleteNoteSuccess()
                isBaseLoading.set(false)

            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
                isBaseLoading.set(false)
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

}