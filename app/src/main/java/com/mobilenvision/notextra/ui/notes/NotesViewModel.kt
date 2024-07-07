package com.mobilenvision.notextra.ui.notes

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.data.local.db.DbCallback
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseViewModel

class NotesViewModel (dataManager: DataManager) : BaseViewModel<NotesNavigator>(dataManager) {
    private var mNoteList: ArrayList<Note> = ArrayList()
    private var noteList: MutableLiveData<List<Note>> = MutableLiveData()
    private var isEmpty = ObservableBoolean()
    var categoryList: ArrayList<Category> = ArrayList()
    var categories: MutableLiveData<List<Category>> = MutableLiveData()

    fun getList() {
        isBaseLoading.set(true)
        dataManager?.getAllNotes(object : DbCallback<List<Note>> {
            override fun onSuccess(result: List<Note>) {
                isBaseLoading.set(false)
                if(result.isEmpty()){
                    isEmpty.set(true)
                }
                else{
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
    fun getCategory(){
        dataManager?.getAllCategories(object : DbCallback<List<Category>> {
            override fun onSuccess(result: List<Category>) {
                categoryList.clear()
                categoryList.addAll(result)
                categories.postValue(categoryList)
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
        dataManager?.updateCategoryOfNote(id,category,object : DbCallback<Boolean> {
            override fun onSuccess(result: Boolean) {
                navigator?.addCategorySuccess()
                isBaseLoading.set(false)

            }
            override fun onError(error: Throwable) {
                navigator?.onFailure(error.message)
                isBaseLoading.set(false)
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
}