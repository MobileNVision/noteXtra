package com.mobilenvision.notextra.utils

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.daily.DailyAdapter
import com.mobilenvision.notextra.ui.notes.NotesAdapter
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object BindingUtils {
    private const val MAX_TEXT_LENGTH = 100
    private const val MAX_TITLE_LENGTH = 25

    @BindingAdapter("noteAdapter")
    @JvmStatic
    fun noteAdapter(recyclerView: RecyclerView, note: List<Note>?) {
        val adapter: NotesAdapter? = recyclerView.adapter as NotesAdapter?
        if (adapter != null && note != null) {
            adapter.addItems(note)
        }
    }
    @BindingAdapter("pagerAdapter")
    @JvmStatic
    fun pagerAdapter(pager: ViewPager2, daily: List<Daily>?) {
        val adapter: DailyAdapter? = pager.adapter as DailyAdapter?
        if (adapter != null && daily != null) {
            adapter.addItems(daily)
        }
    }

    @BindingAdapter("setFontFamily")
    @JvmStatic
    fun setFontFamily(textView: TextView, font: String) {
        val fontResId = when (font) {
            "handwriting" -> {
                R.font.handwriting
            }
            "anandablack" -> {
                R.font.anandablack
            }
            else -> {
                R.font.shiftynotes
            }
        }
        textView.typeface = ResourcesCompat.getFont(textView.context, fontResId)
    }
    @BindingAdapter("categoryListData")
    @JvmStatic
    fun categoryListData(spinner: Spinner, categoryList: List<Category>?) {
        val categoryItems = if (categoryList.isNullOrEmpty()) {
            listOf(spinner.context.getString(R.string.empty_category_message))
        } else {
            listOf(spinner.context.getString(R.string.choose_category)) + categoryList.map { it.name ?: "" }
        }

        val spinnerAdapter = ArrayAdapter(
            spinner.context,
            R.layout.spinner_item,
            categoryItems
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun imageUrl(imageView: RoundedImageView, urlString: String?) {
        if (urlString != null) {
            Thread {
                try {
                    val url = URL(urlString)
                    val connection =
                        url.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(input)
                    (imageView.context as Activity).runOnUiThread {
                        imageView.setBackgroundResource(
                            0
                        )
                    }
                    (imageView.context as Activity).runOnUiThread {
                        imageView.setImageBitmap(
                            bitmap
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        } else {
            imageView.setImageResource(0)
            imageView.setBackgroundDrawable(imageView.context.resources.getDrawable(R.drawable.default_profile))
        }
    }

    @JvmStatic
    @BindingAdapter("onLongClickListener")
    fun onLongClickListener(view: View, listener: View.OnLongClickListener) {
        view.setOnLongClickListener(listener)
    }

    @JvmStatic
    @BindingAdapter("textTitle")
    fun setTextTitle(view: TextView, data: String) {
        view.text = getTrimmedText(data, MAX_TITLE_LENGTH)
    }

    @JvmStatic
    @BindingAdapter("textDescription")
    fun textDescription(view: TextView, data: String) {
        view.text = getTrimmedText(data, MAX_TEXT_LENGTH)
    }

    @JvmStatic
    @BindingAdapter("category")
    fun category(view: TextView, data: String?) {
        if(!data.isNullOrEmpty()){
            view.text = data
        }
        else{
            view.text = "..."
        }
    }

    private fun getTrimmedText(data: String, maxLength: Int): String {
        return if (data.length > maxLength) {
            val endIndex = findLastSpaceIndex(data, maxLength)
            "${data.substring(0, endIndex)} ..."
        } else {
            data
        }
    }

    private fun findLastSpaceIndex(data: String, maxLength: Int): Int {
        val endIndex = data.lastIndexOf(' ', maxLength)
        return if (endIndex != -1) endIndex else maxLength
    }

}
