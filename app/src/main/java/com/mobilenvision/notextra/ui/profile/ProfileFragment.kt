package com.mobilenvision.notextra.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.BR
import com.mobilenvision.notextra.databinding.FragmentProfileBinding
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.ui.base.BaseFragment
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(), ProfileNavigator {
    @Inject
    lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile
    lateinit var currentTheme: String
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        viewModel.setNavigator(this)
        binding.viewModel = viewModel
        currentTheme = mViewModel.getCurrentTheme(baseActivity!!)
        mViewModel.fetchUserProfile()
    }

    companion object {
        const val TAG = "ProfileFragment"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = fetchViewDataBinding()
        showBottomNavigation()
        binding.darkModeCheckBox.isChecked = currentTheme == "DARK_THEME"
    }

    override fun performDependencyInjection(buildComponent: FragmentComponent) {
        buildComponent.inject(this)
    }

    override fun onSaveClick() {
        val isChecked = binding.darkModeCheckBox.isChecked
        val selectedTheme = if (isChecked) "DARK_THEME" else "LIGHT_THEME"
        if(currentTheme != selectedTheme) {
            mViewModel.setCurrentTheme(baseActivity!!, selectedTheme)
            baseActivity?.recreate()
        }
        mViewModel.getUserForUpdate(binding.firstName.text.toString(),binding.lastName.text.toString(),selectedImageUri)
    }
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.profileImage.setImageURI(it)
        }
    }
    override fun onFailure(message: String?) {
        showToast(message!!)
    }

    override fun onImageClick() {
        imagePickerLauncher.launch("image/*")
    }

    override fun onSaveSuccess() {
        showToast(baseActivity!!.getString(R.string.update_success))
    }
}