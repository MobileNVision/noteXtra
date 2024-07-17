package com.mobilenvision.notextra.ui.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.databinding.ActivityRegisterBinding
import com.mobilenvision.notextra.di.component.ActivityComponent
import com.mobilenvision.notextra.ui.base.BaseActivity
import com.mobilenvision.notextra.ui.login.LoginActivity
import javax.inject.Inject

class RegisterActivity @Inject constructor() : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(),
    RegisterNavigator {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private var selectedImageUri: Uri? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_register

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        viewModel.setNavigator(this)
        binding.viewModel = mViewModel
        binding.lifecycleOwner = this
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

    }




    override fun onRegisterSuccess() {
        showToastMessage(getString(R.string.register_success))
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("email", binding.email.text.toString())
            putExtra("password", binding.password.text.toString())
        }
        startActivity(intent)
        finish()
    }

    override fun onRegisterFailure(message: String) {
        showToastMessage(message)
    }

    override fun onImageUploadFailure(message: String) {
        showToastMessage(message)
    }

    override fun onRegisterClick() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        viewModel.registerUser(email, password, firstName, lastName, selectedImageUri)
    }
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.profileImage.setImageURI(it)
        }
    }
    override fun onImageClick() {
        imagePickerLauncher.launch("image/*")
    }

    override fun onBackClick() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun performDependencyInjection(buildComponent: ActivityComponent) {
        buildComponent.inject(this)
    }
}
