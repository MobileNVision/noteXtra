package com.mobilenvision.notextra.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.databinding.ActivityLoginBinding
import com.mobilenvision.notextra.di.component.ActivityComponent
import com.mobilenvision.notextra.ui.base.BaseActivity
import javax.inject.Inject
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.ui.main.MainActivity
import com.mobilenvision.notextra.ui.register.RegisterActivity
import com.mobilenvision.notextra.utils.CommonUtils

class LoginActivity @Inject constructor() : BaseActivity<ActivityLoginBinding, LoginViewModel>(),
    LoginNavigator {
    private val PERMISSION_REQUEST_CODE = 1
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isLogin: Boolean = false

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        mViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        mViewModel.setNavigator(this)
        binding.viewModel = mViewModel
        binding.lifecycleOwner = this
        val emailFromRegistration = intent.getStringExtra("email")
        val passwordFromRegistration = intent.getStringExtra("password")

        if (!emailFromRegistration.isNullOrEmpty() && !passwordFromRegistration.isNullOrEmpty()) {
            binding.email.setText(emailFromRegistration)
            binding.password.setText(passwordFromRegistration)
        }
        binding.rememberMe.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.setRememberMe(isChecked)
        }
        if (mViewModel.getRememberMe()) {
            binding.rememberMe.isChecked = true
            val (userMail, userPassword, id) = mViewModel.getUserData()

            if (!userMail.isNullOrEmpty() && !userPassword.isNullOrEmpty()) {
                binding.email.setText(userMail)
                binding.password.setText(userPassword)
                mViewModel.checkCredentials(userMail, userPassword)
            }
        }

    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isRemember(userId: String) {
        if (mViewModel.getRememberMe()) {
            val userMail = binding.email.text.toString()
            val userPassword = binding.password.text.toString()
            mViewModel.saveUserData(userMail, userPassword, userId)
        }
    }

    override fun onUserLoggedIn(userId: String?) {
        isRemember(userId!!)
        showToastMessage( getString(R.string.login_success))
        redirectToMainActivity()
    }

    override fun onLoginFailed(s: String) {
        showToastMessage(getString(R.string.login_error) + " " + s)
    }

    override fun onLoginClick() {
        isLogin = true
        if (emptyCheck()) {
            checkPermission()
        }
    }

    private fun emptyCheck(): Boolean {
        return if (binding.email.text.isEmpty()) {
            binding.email.error = getString(R.string.empty_error)
            false
        } else if (binding.password.text.isEmpty()) {
            binding.password.error = getString(R.string.empty_error)
            false
        } else {
            true
        }
    }

    private fun login() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        if (!CommonUtils.isInternetAvailable(this)) {
            showToastMessage("Çevrimdışı moddasınız")
            redirectToMainActivity()
        }
        else{
            mViewModel.checkCredentials(email, password)
        }
    }

    override fun onRegisterClick() {
        isLogin = false
        checkPermission()
    }

    override fun onRegistrationSuccess() {
        login()
    }

    override fun onRegistrationFailed(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun appNameClick() {
        mViewModel.changeTheme(this)
        recreate()
    }

    private fun register() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkPermission() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.INTERNET)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        else {
            if (Environment.isExternalStorageManager()) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsToRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            handleLoginOrRegister()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                handleLoginOrRegister()
            } else {
                Toast.makeText(this, getString(R.string.permission_denied_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLoginOrRegister() {
        if (isLogin) {
            login()
        } else {
            register()
        }
    }


    override fun performDependencyInjection(buildComponent: ActivityComponent) {
        buildComponent.inject(this)
    }
}
