package com.mobilenvision.notextra.utils

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
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
        if (data.length > MAX_TITLE_LENGTH) {
            view.text = "${data.substring(0, MAX_TITLE_LENGTH)}..."
        } else {
            view.text = data
        }
    }

    @JvmStatic
    @BindingAdapter("textDescription")
    fun textDescription(view: TextView, data: String) {
        if (data.length > MAX_TITLE_LENGTH) {
            view.text = "${data.substring(0, MAX_TEXT_LENGTH)}..."
        } else {
            view.text = data
        }
    }
}
