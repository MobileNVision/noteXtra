package com.mobilenvision.notextra.utils

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.databinding.ViewDataBinding
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.ui.base.BaseActivity
import com.mobilenvision.notextra.ui.base.BaseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CommonUtils {

    companion object {
        fun <T : ViewDataBinding?, V : BaseViewModel<*>> showLoadingDialog(baseActivity: BaseActivity<T, V>): ProgressDialog {
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

            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        fun getCurrentDateTime(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(java.util.Date())
        }
        fun noteToMap(note: Note): Map<String, Any?> {
            val noteMap = mutableMapOf<String, Any?>()
            noteMap["id"] = note.id
            noteMap["createdAt"] = note.createdAt
            noteMap["text"] = note.text
            noteMap["updatedTime"] = note.updatedTime
            noteMap["title"] = note.title
            noteMap["isSynchronized"] = note.isSynchronized
            noteMap["category"] = note.category
            noteMap["version"] = note.version
            noteMap["updatedAt"] = note.updatedAt
            noteMap["reminderTime"] = note.reminderTime
            noteMap["userId"] = note.userId
            noteMap["priority"] = note.priority
            return noteMap
        }
        fun dailyToMap(daily: Daily): Map<String, Any?> {
            val noteMap = mutableMapOf<String, Any?>()
            noteMap["id"] = daily.id
            noteMap["createdAt"] = daily.createdAt
            noteMap["text"] = daily.text
            noteMap["day"] = daily.day
            noteMap["images"] = daily.images
            noteMap["updatedAt"] = daily.updatedAt
            noteMap["userId"] = daily.userId
            return noteMap
        }

        fun getCurrentDate(): String {
            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
        fun getTomorrowDate(): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        }

        fun getYesterdayDate(): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -2)
            return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        }
        fun getMillisecondsFromString(selectedTime: String): Long {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = dateFormat.parse(selectedTime)
            return date?.time ?: 0L
        }
    }

}