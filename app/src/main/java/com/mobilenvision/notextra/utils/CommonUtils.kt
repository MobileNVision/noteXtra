package com.mobilenvision.notextra.utils

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.databinding.ViewDataBinding
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.ui.base.BaseActivity
import com.mobilenvision.notextra.ui.base.BaseViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mobilenvision.notextra.data.model.db.Note
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
class CommonUtils {

    fun setReminder(context: Context, reminderTime: Long, noteId: String, noteTitle: String) {
        val delay = reminderTime - System.currentTimeMillis()

        val data = Data.Builder()
            .putString("note_id", noteId)
            .putString("note_title", noteTitle)
            .build()

        val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(reminderRequest)
    }
    companion object {
        fun <T : ViewDataBinding?, V : BaseViewModel<*>> showLoadingDialog(baseActivity: BaseActivity<T, V>): ProgressDialog? {
            val progressDialog = ProgressDialog(baseActivity)
            progressDialog.show()
            if (progressDialog.window != null) {
                progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.isIndeterminate = true
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            return progressDialog
        }
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
        }
        fun getCurrentDateTime(): String {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            return sdf.format(java.util.Date())
        }
        fun noteToMap(note: Note): Map<String, Any?> {
            val noteMap = mutableMapOf<String, Any?>()
            noteMap["id"] = note.id.toString()
            noteMap["createdAt"] = note.createdAt
            noteMap["text"] = note.text
            noteMap["updatedTime"] = note.updatedTime
            noteMap["title"] = note.title
            noteMap["isSynchronized"] = note.isSynchronized
            noteMap["category"] = note.category
            noteMap["version"] = note.version
            noteMap["updatedAt"] = note.updatedAt
            noteMap["reminderTime"] = note.reminderTime
            return noteMap
        }
        fun getMillisecondsFromString(selectedTime: String): Long {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = dateFormat.parse(selectedTime)
            return date?.time ?: 0L
        }
    }

}