package com.mobilenvision.notextra.ui.notes

import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.ItemNoteBinding
import com.mobilenvision.notextra.ui.base.BaseViewHolder
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter() : RecyclerView.Adapter<BaseViewHolder>() {
    private lateinit var mNoteAdapterListener: NoteAdapterListener
    private lateinit var context: Context
    private var mNoteList: List<Note> = emptyList()
    private var selectedPosition = RecyclerView.NO_POSITION


    fun getSelectedPosition(): Int {
        return selectedPosition
    }
    constructor(mNoteList: List<Note>) : this() {
        this.mNoteList = mNoteList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemNoteBinding: ItemNoteBinding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ItemViewHolder(itemNoteBinding)
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }


    override fun getItemCount(): Int {
        return if (!mNoteList.isNullOrEmpty()) {
            mNoteList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun addItems(note: List<Note>) {
        mNoteList = note
        Log.d("NotesAdapter", "Notlar eklendi: $mNoteList")
        notifyDataSetChanged()
    }

    fun setListener(noteAdapterListener: NoteAdapterListener) {
        this.mNoteAdapterListener = noteAdapterListener
    }
    fun setContext(context: Context) {
        this.context = context
    }

    inner class ItemViewHolder(binding: ItemNoteBinding) : BaseViewHolder(binding.root),
        NoteItemViewModel.NoteItemViewModelListener {
        private val mBinding: ItemNoteBinding
        private var mItemNoteViewModel: NoteItemViewModel? = null
        init {
            mBinding = binding
        }

        override fun onBind(position: Int) {
            val note: Note = mNoteList[position]
            mItemNoteViewModel = NoteItemViewModel(note, this)
            mBinding.viewModel = mItemNoteViewModel
            mBinding.executePendingBindings()
        }

        override fun onNoteItemClick(note: Note) {
            if (note != null && mNoteAdapterListener != null) {
                mNoteAdapterListener!!.onNoteItemClick(note)
            }
        }

        override fun onNoteItemLongClick(view: View, note: Note) {
            selectedPosition = adapterPosition
            mNoteAdapterListener?.onNoteItemLongClick(view, note)
        }

        override fun onItemCategoryClick(note: Note) {
            if (note != null && mNoteAdapterListener != null) {
                mNoteAdapterListener!!.onItemCategoryClick(note)
            }
        }
    }
    interface NoteAdapterListener {
        fun onNoteItemClick(note: Note)
        fun onNoteItemLongClick(view: View,note: Note)
        fun onItemCategoryClick(note: Note)
    }

}