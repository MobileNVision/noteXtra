package com.mobilenvision.notextra.ui.notes

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Category
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.FragmentNotesBinding
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.ui.addNote.AddNoteFragment
import com.mobilenvision.notextra.ui.base.BaseFragment
import com.mobilenvision.notextra.ui.noteDetail.NoteDetailFragment
import com.mobilenvision.notextra.utils.CommonUtils
import com.mobilenvision.notextra.utils.NoteWidgetProvider
import com.mobilenvision.notextra.utils.WidgetUpdateService
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class NotesFragment : BaseFragment<FragmentNotesBinding, NotesViewModel>(), NotesNavigator, NotesAdapter.NoteAdapterListener {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var userId: String
    private lateinit var noteList: java.util.ArrayList<Note>
    private lateinit var currentNote: Note
    private lateinit var categories: List<String>
    private lateinit var widgetLauncher: ActivityResultLauncher<Intent>

    @Inject
    lateinit var viewModel: NotesViewModel

    private lateinit var notesAdapter: NotesAdapter
    private lateinit var binding: FragmentNotesBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_notes


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNotesBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        viewModel.setNavigator(this)
        binding.viewModel = viewModel
        userId= mViewModel.getUserData().third.toString()
        if(CommonUtils.isInternetAvailable(baseActivity!!)){
            mViewModel.getUnSynchronizedNotes()
            mViewModel.syncDeletedNotes()
        }
        val internetStatus = CommonUtils.isInternetAvailable(baseActivity!!)
        mViewModel.setInternetStatus(internetStatus)
        if(internetStatus){
            mViewModel.getNotesFromFirebase(userId)
        }
        else {
            mViewModel.getListFromDatabase()
        }
        mViewModel.getCategory()
        widgetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                data?.let {
                    val appWidgetId = it.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                    if (appWidgetId != -1) {
                        val sharedPreferences = baseActivity!!.getSharedPreferences("WidgetNotePrefs", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("note_title", currentNote.title)
                        editor.putString("note_text", currentNote.text)
                        editor.putString("note_id", currentNote.id)
                        editor.apply()
                        WidgetUpdateService.startActionUpdateWidget(baseActivity!!)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "NotesFragment"
    }

    override fun onNoteItemClick(note: Note) {
        hideBottomNavigation()
        hideToolbar()
        baseActivity?.loadFragment(NoteDetailFragment.newInstance(note), NoteDetailFragment.TAG)
    }

    override fun onNoteItemLongClick(view: View, note: Note) {
        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.dialog_list_menu, null)

        val dialogBuilder = AlertDialog.Builder(view.context).apply {
            setView(dialogView)
        }

        val alertDialog = dialogBuilder.create()

        dialogView.findViewById<TextView>(R.id.delete).setOnClickListener {
            alertDialog.dismiss()
            deleteNote(note)
        }

        dialogView.findViewById<TextView>(R.id.edit).setOnClickListener {
            alertDialog.dismiss()
            updateNote(note)
        }

        dialogView.findViewById<TextView>(R.id.add_widget).setOnClickListener {
            alertDialog.dismiss()
            addNoteWidget(note)
        }

        dialogView.findViewById<TextView>(R.id.share).setOnClickListener {
            alertDialog.dismiss()
            shareNote()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        alertDialog.window?.setGravity(Gravity.TOP or Gravity.START)
        val layoutParams = alertDialog.window?.attributes
        layoutParams?.x = location[0]
        layoutParams?.y = location[1]
        alertDialog.window?.attributes = layoutParams
    }


    private fun shareNote() {
        val selectedPosition = notesAdapter.getSelectedPosition()
        if (selectedPosition != RecyclerView.NO_POSITION) {
            val viewHolder = binding.notesRecyclerView.findViewHolderForAdapterPosition(selectedPosition)
            if (viewHolder is NotesAdapter.ItemViewHolder) {
                val itemView = viewHolder.itemView
                val screenshotBitmap = takeScreenshot(itemView)
                shareScreenshot(screenshotBitmap)
            }
        } else {
            showToast("Lütfen bir not seçin.")
        }
    }
    private fun takeScreenshot(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }
    private fun shareScreenshot(bitmap: Bitmap) {
        val uri = getImageUri(requireContext(), bitmap)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Ekran Görüntüsünü Paylaş"))
    }

    fun getImageUri(context: Context, image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, image, "Note Screenshot", null)
        return Uri.parse(path)
    }
    private fun updateNote(note: Note) {
        baseActivity?.loadFragment(NoteDetailFragment.newInstance(note), NoteDetailFragment.TAG)
    }

    private fun deleteNote(note: Note) {
        mViewModel.deleteNote(note)
    }

    private fun addNoteWidget(note: Note) {
        val appWidgetManager = AppWidgetManager.getInstance(requireContext())
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(requireContext(), NoteWidgetProvider::class.java))

        if (appWidgetIds.isNotEmpty()) {
            val sharedPreferences = baseActivity!!.getSharedPreferences("WidgetNotePrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("note_title", note.title)
            editor.putString("note_text", note.text)
            editor.putString("note_id", note.id)
            editor.apply()
            WidgetUpdateService.startActionUpdateWidget(baseActivity!!)

        } else {
            requestAppWidget(note)
        }
    }

    private fun requestAppWidget(note: Note) {
        val appWidgetHost = AppWidgetHost(requireContext(), 12)
        val appWidgetId = appWidgetHost.allocateAppWidgetId()

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, ComponentName(requireContext(), NoteWidgetProvider::class.java))
        currentNote = note
        widgetLauncher.launch(intent)
    }


    override fun onItemCategoryClick(note: Note) {
        showDialog(note)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = fetchViewDataBinding()
        setupRecyclerView()
        showBottomNavigation()
        showToolbar()

        binding.smartSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotes(s.toString())
            }
        })
        baseActivity!!.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleOnBackPress()
        }
    }

    private fun handleOnBackPress() {
        activity?.finish()
    }

    private fun filterNotes(query: String) {
        val filteredList = noteList.filter {
            (it.title?.contains(query, ignoreCase = true) ?: false) ||
                    (it.text?.contains(query, ignoreCase = true) ?: false)
        }
        updateAdapter(filteredList)
    }

    private fun updateAdapter(filteredList: List<Note>) {
        notesAdapter = NotesAdapter(emptyList())
        binding.notesRecyclerView.adapter = notesAdapter
        notesAdapter.addItems(filteredList)
        notesAdapter.setListener(this)
        notesAdapter.setContext(requireContext())
        if (filteredList.isEmpty()) {
            binding.textEmptyState.visibility = View.VISIBLE
        } else {
            binding.textEmptyState.visibility = View.GONE
        }
    }

    private fun setupRecyclerView() {
        binding.notesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        notesAdapter = NotesAdapter(emptyList())
        binding.notesRecyclerView.adapter = notesAdapter
        notesAdapter.setListener(this)
        notesAdapter.setContext(requireContext())

    }
    override fun performDependencyInjection(buildComponent: FragmentComponent) {
        buildComponent.inject(this)
    }

    override fun addNoteButtonClick() {
        baseActivity?.loadFragment(AddNoteFragment.newInstance("",""), TAG)

    }

    override fun emptyNoteList() {
        binding.textEmptyState.visibility = View.VISIBLE
        notesAdapter = NotesAdapter()
        noteList = ArrayList()
    }

    override fun setNoteList(mNoteList: ArrayList<Note>) {
        if (mNoteList.isEmpty()) {
            emptyNoteList()
        } else {
            Log.d(TAG, "Note listesi ayarlanıyor: $mNoteList")
            notesAdapter = NotesAdapter(mNoteList)
            notesAdapter.addItems(mNoteList)
            viewModel.addNoteList(mNoteList)
            noteList = mNoteList
            binding.textEmptyState.visibility = View.GONE
        }
    }

    override fun addCategorySuccess() {
        mViewModel.getListFromDatabase()
    }

    override fun onFailure(message: String?) {
        showToast(message!!)
    }

    override fun deleteNoteSuccess() {
        showToast(baseActivity!!.getString(R.string.delete_success))
        mViewModel.getListFromDatabase()
    }

    override fun onFilterClick() {
        showFilterDialog()
    }

    override fun setCategoryList(categoryList: ArrayList<Category>) {
        categories = if(categoryList.size == 0){
            listOf(baseActivity!!.getString(R.string.empty_category_message))
        } else {
            listOf(baseActivity!!.getString(R.string.choose_category)) + categoryList.map {
                it.name ?: ""
            }
        }
    }

    override fun onSuccessAddNotes() {
        showToast(getString(R.string.synchronized_success))
    }

    override fun deleteNoteSuccessToDatabase() {
        showToast(getString(R.string.delete_to_database_success))
    }

    private fun showFilterDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val minDateTextView: TextView = dialogView.findViewById(R.id.minDateTextView)
        val maxDateTextView: TextView = dialogView.findViewById(R.id.maxDateTextView)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.spinner_category)
        val priorityRadioGroup: RadioGroup = dialogView.findViewById(R.id.radioGroupPriority)
        val filterButton: Button = dialogView.findViewById(R.id.filter_button)

        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        minDateTextView.setOnClickListener {
            showDatePickerDialog(minDateTextView)
        }

        maxDateTextView.setOnClickListener {
            showDatePickerDialog(maxDateTextView)
        }

        filterButton.setOnClickListener {
            applyFilters(
                minDateTextView.text.toString(),
                maxDateTextView.text.toString(),
                categorySpinner.selectedItem as String,
                when (priorityRadioGroup.checkedRadioButtonId) {
                    R.id.radioButtonLow -> baseActivity!!.getString(R.string.low)
                    R.id.radioButtonMedium -> baseActivity!!.getString(R.string.medium)
                    R.id.radioButtonHigh -> baseActivity!!.getString(R.string.high)
                    else -> ""
                }
            )
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                textView.text = dateFormat.format(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun applyFilters(minDate: String, maxDate: String, selectedCategory: String, selectedPriority: String) {
        val filteredList = noteList.filter { note ->
            val dateMatch = (minDate.isEmpty() || note.updatedTime!! >= minDate) &&
                    (maxDate.isEmpty() || note.updatedTime!! <= maxDate)
            val categoryMatch = selectedCategory == baseActivity!!.getString(R.string.choose_category) || note.category == selectedCategory
            val priorityMatch = selectedPriority.isEmpty() || note.priority == selectedPriority

            dateMatch && categoryMatch && priorityMatch
        }
        updateAdapter(filteredList)
    }

    private fun showDialog(note: Note) {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_select_category, null)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.category_spinner)
        val categories = mutableListOf<String>()
        if(mViewModel.getCategories()?.isEmpty() == true){
            categories.add(baseActivity!!.getString(R.string.empty_category))
        }
        else{
            categories.add(baseActivity!!.getString(R.string.choose_category))
            mViewModel.getCategories()?.forEach {
                it.name?.let { name ->
                    categories.add(name)
                }
            }
        }

        adapter = ArrayAdapter(
            baseActivity!!,
            R.layout.spinner_item,
            categories
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        categorySpinner.adapter = adapter


        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton(baseActivity!!.getString(R.string.select)) { dialog, _ ->
                var category = ""
                if(categorySpinner.selectedItemPosition != 0){
                    category = categorySpinner.selectedItem.toString()
                    }
                mViewModel.updateCategory(note.id,category)
                dialog.dismiss()
            }
            .setNegativeButton(baseActivity!!.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        builder.show()
    }


}