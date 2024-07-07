package com.mobilenvision.notextra.ui.notes

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
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
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.FragmentNotesBinding
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.ui.addNote.AddNoteFragment
import com.mobilenvision.notextra.ui.base.BaseFragment
import com.mobilenvision.notextra.ui.noteDetail.NoteDetailFragment
import com.mobilenvision.notextra.utils.NoteWidgetProvider
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class NotesFragment : BaseFragment<FragmentNotesBinding, NotesViewModel>(), NotesNavigator, NotesAdapter.NoteAdapterListener {

    private lateinit var currentNote: Note

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
        mViewModel.getList()
        mViewModel.getCategory()
    }

    companion object {
        const val TAG = "NotesFragment"
    }

    override fun onNoteItemClick(note: Note) {
        hideBottomNavigation()
        hideToolbar()
        baseActivity?.loadFragment(NoteDetailFragment.newInstance(note), TAG)
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
        baseActivity?.loadFragment(NoteDetailFragment.newInstance(note), TAG)
    }

    private fun deleteNote(note: Note) {
        mViewModel.deleteNote(note)
    }

    private fun addNoteWidget(note: Note) {
        val appWidgetManager = AppWidgetManager.getInstance(requireContext())
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(requireContext(), NoteWidgetProvider::class.java))

        if (appWidgetIds.isNotEmpty()) {
            NoteWidgetProvider.updateAppWidget(requireContext(), appWidgetManager, appWidgetIds[0], note)
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

        startActivityForResult(intent, 13)
        currentNote = note
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 && resultCode == RESULT_OK) {
            data?.let {
                val appWidgetId = it.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                if (appWidgetId != -1 && currentNote != null) {
                    NoteWidgetProvider.updateAppWidget(requireContext(), AppWidgetManager.getInstance(requireContext()), appWidgetId, currentNote!!)
                }
            }
        }
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
        notesAdapter = NotesAdapter(emptyList())
        binding.notesRecyclerView.adapter = notesAdapter
        notesAdapter.setListener(this)
        notesAdapter.setContext(requireContext())
    }
    private fun setupRecyclerView() {
        binding.notesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        notesAdapter = NotesAdapter(emptyList())
        binding.notesRecyclerView.adapter = notesAdapter
    }
    override fun performDependencyInjection(buildComponent: FragmentComponent) {
        buildComponent.inject(this)
    }

    override fun addNoteButtonClick() {
        baseActivity?.loadFragment(AddNoteFragment.newInstance("",""), TAG)

    }

    override fun emptyNoteList() {
        binding.textEmptyState.visibility = View.VISIBLE
    }

    override fun setNoteList(mNoteList: ArrayList<Note>) {
        if (mNoteList.isEmpty()) {
            emptyNoteList()
        } else {
            Log.d(TAG, "Note listesi ayarlanıyor: $mNoteList")
            notesAdapter = NotesAdapter(mNoteList)
            notesAdapter.addItems(mNoteList)
            viewModel.addNoteList(mNoteList)
            binding.textEmptyState.visibility = View.GONE
        }
    }

    override fun addCategorySuccess() {
        mViewModel.getList()
    }

    override fun onFailure(message: String?) {
        showToast(message!!)
    }

    override fun deleteNoteSuccess() {
        mViewModel.getList()
    }

    private fun showDialog(note: Note) {
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_select_category, null)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.category_spinner)
        val categories = mutableListOf<String>()
        categories.add(baseActivity!!.getString(R.string.choose_category))

        mViewModel.getCategories()?.forEach {
            it.name?.let { name ->
                categories.add(name)
            }
        }

        val adapter = ArrayAdapter(
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