package com.mobilenvision.notextra.ui.main

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.data.model.db.Note
import com.mobilenvision.notextra.databinding.ActivityMainBinding
import com.mobilenvision.notextra.di.component.ActivityComponent
import com.mobilenvision.notextra.ui.addNote.AddNoteFragment
import com.mobilenvision.notextra.ui.base.BaseActivity
import com.mobilenvision.notextra.ui.login.LoginActivity
import com.mobilenvision.notextra.ui.noteDetail.NoteDetailFragment
import com.mobilenvision.notextra.ui.notes.NotesFragment
import com.mobilenvision.notextra.ui.profile.ProfileFragment
import java.util.Locale
import javax.inject.Inject


class MainActivity @Inject constructor() : BaseActivity<ActivityMainBinding, MainViewModel>(),
    MainNavigator {

    private lateinit var binding: ActivityMainBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_main
    private lateinit var speechRecognizerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mViewModel.setNavigator(this)
        binding.viewModel = mViewModel
        binding.lifecycleOwner = this
        loadFragment(NotesFragment(), NotesFragment.TAG)
        handleIntent(intent)
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.notes -> {
                    loadFragment(NotesFragment(), NotesFragment.TAG)
                    true
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment(),ProfileFragment.TAG)
                    true
                }
                else -> {
                    false
                }
            }
        }
        createNotificationChannel()
        speechRecognizerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val speechResult = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!speechResult.isNullOrEmpty()) {
                    val text = Editable.Factory.getInstance().newEditable(speechResult[0])
                    val fragment = AddNoteFragment.newInstance(text.toString(), "")
                    loadFragment(fragment, AddNoteFragment.TAG)
                }
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            if (currentFragment != null) {
                when (currentFragment.tag) {
                    NoteDetailFragment.TAG -> {
                        binding.microphoneLayout.visibility = View.GONE
                    }
                    else -> {
                        binding.microphoneLayout.visibility = View.VISIBLE
                    }
                }
            }
        }

    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for Note Reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("reminderChannel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun handleIntent(intent: Intent) {
        val intent = intent
        if (Intent.ACTION_VIEW == intent.action) {
            val noteText = intent.getStringExtra("noteText")
            if (!noteText.isNullOrEmpty()) {
                val fragment = AddNoteFragment.newInstance(noteText, "")
                loadFragment(fragment, AddNoteFragment.TAG)
            } else {
                val fragment = AddNoteFragment.newInstance("", "")
                loadFragment(fragment, AddNoteFragment.TAG)
            }
        }
        intent.extras?.let { extras ->
            if (extras.containsKey("noteId")) {
                val noteId = extras.getString("noteId", "-1")
                if (noteId != "-1") {
                    mViewModel.getNote(noteId)
                }
            }
        }
    }



    override fun onFragmentDetached(tag: String?) {
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            fragmentManager
                .beginTransaction()
                .disallowAddToBackStack()
                .remove(fragment)
                .commitNow()
        }
    }

    override fun performDependencyInjection(buildComponent: ActivityComponent) {
        buildComponent.inject(this)
    }

    override fun onSuccess(message: String) {
        Log.e("İşlem Başarılı",message)
    }

    override fun onError(message: String) {
        Log.e("İşlem Başarısız",
            message
        )
    }

    override fun onLogoutSuccess() {
        showToastMessage(getString(R.string.logout_success))
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun onFailure(message: String?) {
        showToastMessage(message!!)
    }

    override fun setNote(result: Note) {
        loadFragment(NoteDetailFragment.newInstance(result), NoteDetailFragment.TAG)
    }

    override fun onMicrophoneClick() {
        sendMicrophoneMessage()
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
            showToastMessage(getString(R.string.speech_not_supported))
        }
    }
}