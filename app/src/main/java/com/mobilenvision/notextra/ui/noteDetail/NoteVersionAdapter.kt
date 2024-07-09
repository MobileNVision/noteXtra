package com.mobilenvision.notextra.ui.noteDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.ItemNoteVersionBinding
import com.mobilenvision.notextra.ui.base.BaseViewHolder

class NoteVersionsAdapter(): RecyclerView.Adapter<BaseViewHolder>() {
    private lateinit var mNoteVersionsAdapterListener: NoteVersionsAdapterListener
    private var mNoteList: List<Note> = emptyList()


    constructor(mNoteList: List<Note>) : this() {
        this.mNoteList = mNoteList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemNoteVersionBinding: ItemNoteVersionBinding = ItemNoteVersionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ItemViewHolder(itemNoteVersionBinding)
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }


    override fun getItemCount(): Int {
        return if (mNoteList.isNotEmpty()) {
            mNoteList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun setListener(noteAdapterListener: NoteVersionsAdapterListener) {
        this.mNoteVersionsAdapterListener = noteAdapterListener
    }


    inner class ItemViewHolder(binding: ItemNoteVersionBinding) : BaseViewHolder(binding.root),
        NoteVersionViewModel.NoteItemViewModelListener {
        private val mBinding: ItemNoteVersionBinding
        private var mItemNoteViewModel: NoteVersionViewModel? = null
        init {
            mBinding = binding
        }

        override fun onBind(position: Int) {
            val note: Note = mNoteList[position]
            mItemNoteViewModel = NoteVersionViewModel(note, this)
            mBinding.viewModel = mItemNoteViewModel
            mBinding.executePendingBindings()
        }

        override fun onNoteItemClick(note: Note) {
            mNoteVersionsAdapterListener.onNoteItemClick(note)
        }


    }
    interface NoteVersionsAdapterListener {
        fun onNoteItemClick(note: Note)
    }

}