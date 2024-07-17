package com.mobilenvision.notextra.ui.daily

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.databinding.ItemDailyBinding
import com.mobilenvision.notextra.ui.addDaily.ImagesAdapter
import com.mobilenvision.notextra.ui.base.BaseViewHolder

class DailyAdapter() : RecyclerView.Adapter<BaseViewHolder>() {
    private lateinit var mDailyAdapterListener: DailyAdapterListener
    private lateinit var context: Context
    private var mDailyList: List<Daily> = emptyList()
    private var font: String = ""
    private var selectedPosition = RecyclerView.NO_POSITION
    private lateinit var imageAdapter: ImagesAdapter


    fun getSelectedPosition(): Int {
        return selectedPosition
    }
    constructor(mDailyList: List<Daily>, font: String) : this() {
        this.mDailyList = mDailyList
        this.font = font
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemDailyBinding: ItemDailyBinding = ItemDailyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ItemViewHolder(itemDailyBinding)
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }


    override fun getItemCount(): Int {
        return if (mDailyList.isNotEmpty()) {
            mDailyList.size
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun addItems(note: List<Daily>) {
        mDailyList = note
        notifyDataSetChanged()
    }

    fun setListener(noteAdapterListener: DailyAdapterListener) {
        this.mDailyAdapterListener = noteAdapterListener
    }
    fun setContext(context: Context) {
        this.context = context
    }

    fun getItem(position: Int): Daily? {
        if (position < mDailyList.size) {
            return mDailyList[position]
        }
        return null
    }
    inner class ItemViewHolder(binding: ItemDailyBinding) : BaseViewHolder(binding.root),
        DailyItemViewModel.DailyItemViewModelListener, ImagesAdapter.ImagesAdapterListener {

        private val mBinding: ItemDailyBinding
        private var mItemDailyViewModel: DailyItemViewModel? = null
        private lateinit var mContext: Context

        init {
            mBinding = binding
            mContext = binding.root.context
            mDailyAdapterListener = mContext as DailyAdapterListener
        }
        override fun onBind(position: Int) {
            val note: Daily = mDailyList[position]
            mItemDailyViewModel = DailyItemViewModel(note, this)
            mBinding.viewModel = mItemDailyViewModel
            mItemDailyViewModel!!.setFont(font)
            mBinding.executePendingBindings()
            imageAdapter = ImagesAdapter()
            imageAdapter.setListener(this)
            val images = mDailyList.getOrNull(position)?.images
            if (!images.isNullOrEmpty()) {
                imageAdapter.addImage(images)
            }
            mBinding.recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = imageAdapter
            }
        }

        override fun onDailyItemClick(daily: Daily) {
            mDailyAdapterListener.onDailyItemClick(daily)
        }

        override fun onImagesItemClick(imageUrl: String) {
            mDailyAdapterListener.onImagesItemClick(imageUrl)
        }

        override fun onImagesItemLongClick(imageUrl: String) {
        }
    }

    interface DailyAdapterListener {
        fun onDailyItemClick(daily: Daily)
        fun onImagesItemClick(imageUrl: String)
    }


}