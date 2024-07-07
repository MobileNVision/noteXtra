package com.mobilenvision.notextra.ui.addNote

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.FragmentAddNoteBinding
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.ui.base.BaseFragment
import com.mobilenvision.notextra.ui.notes.NotesFragment
import com.mobilenvision.notextra.utils.CommonUtils
import com.mobilenvision.notextra.utils.ReminderReceiver
import java.util.Calendar
import javax.inject.Inject


class AddNoteFragment : BaseFragment<FragmentAddNoteBinding, AddNoteViewModel>(), AddNoteNavigator {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var categories: List<String>
    private var notePriority: String = "Low"

    @Inject
    lateinit var viewModel: AddNoteViewModel
    lateinit var category: Category
    lateinit var note: Note
    private var selectedTime: String = Calendar.getInstance().time.toString()

    private lateinit var binding: FragmentAddNoteBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_add_note


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddNoteBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AddNoteViewModel::class.java]
        viewModel.setNavigator(this)
        binding.viewModel = viewModel
        mViewModel.getCategory()
        mViewModel.setIsInternetAvailable(CommonUtils.isInternetAvailable(baseActivity!!))

        val note = arguments?.getString("note", "") ?: ""
        binding.noteDescription.setText(note)
        selectedTime = arguments?.getString("reminderTime", "") ?: ""

    }

    private fun handleOnBackPress() {
        loadFragment(NotesFragment(), NotesFragment.TAG)
    }
    companion object {
        fun newInstance(note: String, reminderTime: String): AddNoteFragment {
            val fragment = AddNoteFragment()
            val args = Bundle()
            args.putString("note", note)
            args.putString("reminderTime", reminderTime)
            fragment.arguments = args
            return fragment
        }
        const val TAG = "AddNoteFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = fetchViewDataBinding()
        showBottomNavigation()
        showToolbar()
        binding.noteReminderTime.setOnLongClickListener {
            showDeleteReminderDialog()
            true
        }
        baseActivity!!.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleOnBackPress()
        }
        setupPriorityRadioButtons()
    }
    override fun performDependencyInjection(buildComponent: FragmentComponent) {
        buildComponent.inject(this)
    }



    private fun setupPriorityRadioButtons() {
        binding.radioButtonLow.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)  notePriority = getString(R.string.low)
        }

        binding.radioButtonMedium.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) notePriority = getString(R.string.medium)
        }

        binding.radioButtonHigh.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) notePriority = getString(R.string.high)
        }
    }

    override fun setCategoryList(categoryList: ArrayList<Category>) {
        if(categoryList.size == 0){
            categories = listOf(baseActivity!!.getString(R.string.empty_category_message))
            adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                categories
            )
        }
        else {
            categories =
                listOf(baseActivity!!.getString(R.string.choose_category)) + categoryList.map {
                    it.name ?: ""
                }

            adapter = ArrayAdapter(
                baseActivity!!,
                R.layout.spinner_item,
                categories
            )
        }
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    override fun onSaveClick() {
        val title = binding.noteTitle.text.toString()
        val description = binding.noteDescription.text.toString()
        var category = "";
        if(binding.spinnerCategory.selectedItemPosition != 0){
        category = binding.spinnerCategory.selectedItem?.toString()!!
        }
        val updatedTime = CommonUtils.getCurrentDateTime()

        if (description.isNotEmpty()) {
            note = Note(
                title = title.takeIf { it.isNotEmpty() },
                text = description,
                category = category.takeIf { it?.isNotEmpty() ?: true },
                reminderTime = selectedTime.takeIf { it.isNotEmpty() },
                updatedTime = updatedTime,
                version= 1,
                userId = mViewModel.getUserData().third,
                priority = notePriority

            )
            mViewModel.insertNote(note)
        } else {
            baseActivity?.let { showToast(it.getString(R.string.please_fill_all_fields_and_select_category)) }
        }
    }

    override fun onSuccessAddNote() {
        if(!selectedTime.isNullOrEmpty()){
        requestExactAlarmPermission()}
        loadFragment(NotesFragment(), NotesFragment.TAG)
    }

    override fun onFailure(message: String?) {
        showToast(message!!)
    }

    override fun onAddCategoryClick() {
            showDialog(false)
    }

    override fun onSuccessAddCategory() {
        mViewModel.getCategory()
    }

    override fun onAddReminderClick() {
        showDateTimePicker()
    }

    override fun onEditCategoryClick() {
        if(binding.spinnerCategory.selectedItemPosition != 0) {
            showDialog(true)
        }
        else{
            showToast(baseActivity!!.getString(R.string.please_choose_category))
        }
    }

    override fun onSuccessUpdateCategory() {
        mViewModel.getCategory()
        showToast(baseActivity!!.getString(R.string.update_success))
    }

    override fun onDeleteCategoryClick() {
        mViewModel.deleteCategoryByName(binding.spinnerCategory.selectedItem.toString())
    }

    override fun onSuccessDeleteCategory() {
        showToast(baseActivity!!.getString(R.string.delete_success))
    }

    override fun onSuccessAddNoteToDatabase() {
        showToast(baseActivity!!.getString(R.string.success_load_to_database))
    }

    private fun showDialog(isUpdate: Boolean) {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_category, null)

        var exCategoryName=""
        val editTextCategory = dialogView.findViewById<EditText>(R.id.editTextCategory)
        if(isUpdate){
            exCategoryName = binding.spinnerCategory.selectedItem.toString()
            editTextCategory.setText(exCategoryName)
        }
        val positiveButtonText = if (isUpdate) baseActivity!!.getString(R.string.update) else baseActivity!!.getString(R.string.add)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                val newCategoryName = editTextCategory.text.toString().trim()
                if(!isUpdate){
                    mViewModel.insertCategory(newCategoryName)
                    dialog.dismiss()
                }
                else if (newCategoryName.isNotEmpty()) {
                    mViewModel.updateCategory(newCategoryName,exCategoryName)
                    dialog.dismiss()
                } else {
                    showToast(baseActivity!!.getString(R.string.empty_category_name))
                }
            }
            .setNegativeButton(baseActivity!!.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        builder.show()
    }

    private fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val today = Calendar.getInstance()
                if (selectedDate.before(today)) {
                    showToast( baseActivity!!.getString(R.string.date_picker_error_message))
                    return@DatePickerDialog
                }
                TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDate.set(Calendar.MINUTE, minute)
                        onDateTimeSelected(selectedDate)
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    true
                ).apply {
                    val currentHour = currentDate.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = currentDate.get(Calendar.MINUTE)
                    if (selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        selectedDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        selectedDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
                    ) {
                        updateTime(currentHour, currentMinute)
                    }
                    show()
                }
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun onDateTimeSelected(selectedDate: Calendar) {
        val formattedDay = String.format("%02d", selectedDate.get(Calendar.DAY_OF_MONTH))
        val formattedMonth = String.format("%02d", selectedDate.get(Calendar.MONTH) + 1)
        val formattedHour = String.format("%02d", selectedDate.get(Calendar.HOUR_OF_DAY))
        val formattedMinute = String.format("%02d", selectedDate.get(Calendar.MINUTE))
        selectedTime = "${formattedDay}/${formattedMonth}/${selectedDate.get(Calendar.YEAR)} " +
                "${formattedHour}:${formattedMinute}"
        binding.noteReminderTime.text = selectedTime
    }

    private fun showDeleteReminderDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(baseActivity!!.getString(R.string.delete_reminder_title))
            .setMessage(baseActivity!!.getString(R.string.delete_reminder_message))
            .setPositiveButton(baseActivity!!.getString(R.string.yes)) { _, _ ->
                deleteReminder()
            }
            .setNegativeButton(baseActivity!!.getString(R.string.no), null)
            .show()
    }

    private fun deleteReminder() {
        binding.noteReminderTime.text = ""
        selectedTime = ""
        showToast(baseActivity!!.getString(R.string.delete_reminder_success))
    }

    private val requestExactAlarmPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            requestNotificationPermission()
        } else {
            showToast(requireContext().getString(R.string.permission_denied_message))
        }
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setReminder()
        } else {
            showToast(requireContext().getString(R.string.permission_denied_message))
        }
    }

    private fun setReminder() {
        val intent = Intent(baseActivity, ReminderReceiver::class.java).apply {
            putExtra("noteTitle", note.title)
            putExtra("noteId", note.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(baseActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = baseActivity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, CommonUtils.getMillisecondsFromString(selectedTime), pendingIntent)
            } catch (e: SecurityException) {
                showToast(baseActivity!!.getString(R.string.security_exception_message, e.message))
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, CommonUtils.getMillisecondsFromString(selectedTime), pendingIntent)
        }
    }

    private fun requestExactAlarmPermission() {
        val alarmManager = baseActivity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager!!.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.data = Uri.fromParts("package", requireActivity().packageName, null)
            requestExactAlarmPermissionLauncher.launch(intent)
        }
        else {
            setReminder()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                baseActivity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = notificationManager.getNotificationChannel("reminderChannel")

            if (channel.importance == NotificationManager.IMPORTANCE_NONE) {

                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, baseActivity!!.packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, channel.id)
                }
                requestNotificationPermissionLauncher.launch(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            11 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setReminder()
                } else {
                    showToast(baseActivity!!.getString(R.string.permission_neccesary))
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}