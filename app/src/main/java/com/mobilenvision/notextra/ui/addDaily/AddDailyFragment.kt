package com.mobilenvision.notextra.ui.addDaily

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Daily
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.FragmentAddDailyBinding
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.ui.base.BaseFragment
import com.mobilenvision.notextra.ui.daily.DailyFragment
import com.mobilenvision.notextra.ui.notes.NotesFragment
import com.mobilenvision.notextra.utils.CommonUtils
import java.util.Locale
import java.util.UUID
import javax.inject.Inject


class AddDailyFragment : BaseFragment<FragmentAddDailyBinding, AddDailyViewModel>(), AddDailyNavigator, ImagesAdapter.ImagesAdapterListener {

    private var dailyId: String = ""

    @Inject
    lateinit var viewModel: AddDailyViewModel
    lateinit var note: Note
    private var selectedTime: String = ""
    private var selectedImageUris = mutableListOf<String>()
    private lateinit var imageAdapter: ImagesAdapter

    private lateinit var binding: FragmentAddDailyBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_add_daily
    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddDailyBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AddDailyViewModel::class.java]
        viewModel.setNavigator(this)
        binding.viewModel = viewModel
        mViewModel.setIsInternetAvailable(CommonUtils.isInternetAvailable(baseActivity!!))
        mViewModel.getFontFamily()

        speechRecognizerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val speechResult = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!speechResult.isNullOrEmpty()) {
                    val text = Editable.Factory.getInstance().newEditable(speechResult[0])
                        val currentText = binding.noteDescription.text.toString()
                        val combinedText = Editable.Factory.getInstance().newEditable("$currentText $text")
                        binding.noteDescription.text = combinedText
                }
            }
        }
    }

    private fun handleOnBackPress() {
        loadFragment(NotesFragment(), NotesFragment.TAG)
    }
    companion object {
        fun newInstance(note: String, reminderTime: String): AddDailyFragment {
            val fragment = AddDailyFragment()
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
        baseActivity!!.onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleOnBackPress()
        }
        val note = arguments?.getString("note", "") ?: ""
        binding.noteDescription.setText(note)
        selectedTime = arguments?.getString("reminderTime", "") ?: ""

        imageAdapter = ImagesAdapter()
        imageAdapter.setListener(this)
        imageAdapter.addImage(selectedImageUris)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        if (CommonUtils.isInternetAvailable(requireContext())) {
            mViewModel.loadFromFirebase(mViewModel.getUserData().third!!,CommonUtils.getCurrentDate())
        } else {
            mViewModel.loadFromDatabase(CommonUtils.getCurrentDate())
        }
    }
    override fun performDependencyInjection(buildComponent: FragmentComponent) {
        buildComponent.inject(this)
    }
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUris.add(it.toString())
            imageAdapter.addImage(selectedImageUris)
        }
    }


    override fun onSaveClick() {
        val description = binding.noteDescription.text.toString()
        val day = CommonUtils.getCurrentDate()
        if(dailyId.isEmpty()){
            dailyId = UUID.randomUUID().toString()
        }
        val daily = Daily(dailyId,description,day,selectedImageUris,mViewModel.getUserData().third)
        if (description.isNotEmpty()) {
            mViewModel.insertDaily(daily)
        } else {
            baseActivity?.let { showToast(it.getString(R.string.please_fill_all_fields_and_select_category)) }
        }
    }

    override fun onSuccessAddNote() {
        showToast(baseActivity!!.getString(R.string.success_load))
        loadFragment(DailyFragment(), DailyFragment.TAG)
    }

    override fun onFailure(message: String?) {
        showToast(message!!)
    }

    override fun onSuccessAddNoteToDatabase() {
        showToast(baseActivity!!.getString(R.string.success_load_to_database))
        loadFragment(DailyFragment(), DailyFragment.TAG)
    }

    private fun sendMicrophoneMessage(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (a: ActivityNotFoundException) {
            showToast(baseActivity!!.getString(R.string.speech_not_supported))
        }
    }
    override fun onNoteMicrophoneClick() {
        sendMicrophoneMessage()
    }

    override fun onAddImageClick() {
        imagePickerLauncher.launch("image/*")
    }

    override fun setDaily(day: Daily) {
        binding.noteDescription.setText(day.text)
        val images = day.images ?: emptyList()
        selectedImageUris = images.toMutableList()
        imageAdapter.addImage(selectedImageUris)
        dailyId = day.id
    }

    override fun onImagesItemClick(imageUrl: String) {
        FullScreenImageDialogFragment.newInstance(imageUrl)
            .show(baseActivity!!.supportFragmentManager, "FullScreenImageDialogFragment")
    }

    override fun onImagesItemLongClick(imageUrl: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(baseActivity!!.getString(R.string.delete_image_message))
            .setPositiveButton(baseActivity!!.getString(R.string.delete)) { _, _ ->
                selectedImageUris.remove(imageUrl)
                imageAdapter.addImage(selectedImageUris)
            }
            .setNegativeButton(baseActivity!!.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()    }

}