package com.mobilenvision.notextra.ui.noteDetail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.FragmentNoteDetailBinding
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.ui.base.BaseFragment
import com.mobilenvision.notextra.ui.notes.NotesFragment
import com.mobilenvision.notextra.utils.CommonUtils
import java.util.Calendar
import javax.inject.Inject


class NoteDetailFragment : BaseFragment<FragmentNoteDetailBinding, NoteDetailViewModel>(), NoteDetailNavigator {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var categories: List<String>
    private var notePriority: String = "Low"
    lateinit var category: String
    private var selectedTime: String = Calendar.getInstance().time.toString()
    lateinit var note: Note

    @Inject
    lateinit var viewModel: NoteDetailViewModel

    private lateinit var binding: FragmentNoteDetailBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_note_detail


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNoteDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[NoteDetailViewModel::class.java]
        viewModel.setNavigator(this)
        binding.viewModel = viewModel

        mViewModel.getCategory()


    }

    companion object {
        fun newInstance(note: Note): NoteDetailFragment {
            val args = Bundle()
            val fragment =
                NoteDetailFragment()
            args.putSerializable("note", note)
            fragment.arguments = args
            return fragment
        }

        const val TAG = "NoteDetailFragment"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = fetchViewDataBinding()
        showBottomNavigation()
        showToolbar()
        setupPriorityRadioButtons()
        baseActivity!!.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleOnBackPress()
        }
    }

    private fun initNote(){
        note = (arguments?.getSerializable("note") as? Note)!!
        if (note != null) {
            binding.noteTitle.setText(note.title)
            binding.noteDescription.setText(note.text)
            binding.noteReminderTime.text = note.reminderTime
            binding.spinnerCategory.setSelection(categories.indexOf(note.category))
            when (note.priority) {
                baseActivity!!.getString(R.string.low) -> binding.radioButtonLow.isChecked = true
                baseActivity!!.getString(R.string.medium) -> binding.radioButtonMedium.isChecked = true
                baseActivity!!.getString(R.string.high) -> binding.radioButtonHigh.isChecked = true
            }
        }
    }

    private fun handleOnBackPress() {
        loadFragment(NotesFragment(), NotesFragment.TAG)
    }

    override fun performDependencyInjection(buildComponent: FragmentComponent) {
        buildComponent.inject(this)
    }

    override fun onFailure(message: String?) {
        showToast(message!!)
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
        initNote()
    }

    override fun onEditClick() {
        if(binding.spinnerCategory.selectedItemPosition != 0){
            category = binding.spinnerCategory.selectedItem?.toString()!!
        }
        val updatedTime = CommonUtils.getCurrentDateTime()

        var note = Note(
            title = binding.noteTitle.text.toString(),
            text = binding.noteDescription.text.toString(),
            category = category.takeIf { it?.isNotEmpty() ?: true },
            reminderTime = selectedTime.takeIf { it.isNotEmpty() },
            updatedTime = updatedTime,
            version = 1,
            userId = mViewModel.getUserData().third,
            priority = notePriority

        )
        mViewModel.updateNote(note)
    }

    override fun onDeleteClick() {
        mViewModel.deleteNote(note)
    }

    override fun addReminderClick() {
        showDateTimePicker()
    }

    override fun onDeleteCategoryClick() {
        mViewModel.deleteCategoryByName(binding.spinnerCategory.selectedItem.toString())

    }

    override fun onAddCategoryClick() {
        showDialog(false)
    }

    override fun onEditCategoryClick() {
        if(binding.spinnerCategory.selectedItemPosition != 0) {
            showDialog(true)
        }
        else{
            showToast(baseActivity!!.getString(R.string.please_choose_category))
        }
    }

    override fun onSuccessUpdateNote() {
        loadFragment(NotesFragment(), NotesFragment.TAG)
    }

    override fun deleteNoteSuccess() {
        loadFragment(NotesFragment(), NotesFragment.TAG)
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
    override fun onSuccessAddCategory() {
        mViewModel.getCategory()
    }

    override fun onSuccessUpdateCategory() {
        mViewModel.getCategory()
        showToast(baseActivity!!.getString(R.string.update_success))
    }

    override fun onSuccessDeleteCategory() {
        showToast(baseActivity!!.getString(R.string.delete_success))
    }
}