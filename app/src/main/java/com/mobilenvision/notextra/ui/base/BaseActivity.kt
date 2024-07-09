package com.mobilenvision.notextra.ui.base


import android.app.FragmentManager
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobilenvision.notextra.NoteXtraApplication
import com.mobilenvision.notextra.R
import com.mobilenvision.notextra.di.component.ActivityComponent
import com.mobilenvision.notextra.di.component.DaggerActivityComponent
import com.mobilenvision.notextra.di.module.ActivityModule
import com.mobilenvision.notextra.ui.login.LoginActivity
import com.mobilenvision.notextra.ui.notes.NotesFragment
import com.mobilenvision.notextra.ui.profile.ProfileFragment
import com.mobilenvision.notextra.utils.CommonUtils
import javax.inject.Inject


abstract class BaseActivity<T : ViewDataBinding?, V : BaseViewModel<*>> :
    AppCompatActivity(), BaseFragment.Callback {
    @Inject
    protected lateinit var mViewModel: V
    private var mProgressDialog: ProgressDialog? = null
    private var viewDataBinding: T? = null


    abstract val bindingVariable: Int

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onFragmentAttached() {}
    override fun onFragmentDetached(tag: String?) {}

    fun checkCurrentFragment(fragmentManager: FragmentManager): String? {
        var stackFragment: String? = null
        if (fragmentManager.findFragmentByTag(ProfileFragment.TAG) != null) {
            stackFragment = ProfileFragment.TAG
        }
        else if (fragmentManager.findFragmentByTag(NotesFragment.TAG) != null) {
            stackFragment = NotesFragment.TAG
        }
        return stackFragment
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection(getBuildComponent())
        super.onCreate(savedInstanceState)
        performDataBinding()

        val currentTheme = mViewModel.getCurrentTheme(this)
        setAppTheme(currentTheme)
    }
    abstract fun performDependencyInjection(buildComponent: ActivityComponent)
    private fun getBuildComponent(): ActivityComponent {
        return DaggerActivityComponent.builder()
            .appComponent((application as NoteXtraApplication).appComponent)
            .activityModule(ActivityModule(this))
            .build()
    }

    private fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }


    fun showLoading() {
        hideLoading()
        mProgressDialog = CommonUtils.showLoadingDialog(this)
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }



     fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
         finish()
    }

    fun loadFragment(fragment: Fragment, fragmentTag: String){
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                fragment,
                fragmentTag
            )
            .addToBackStack(fragmentTag)
            .commit()
    }
    fun showToastMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
        }
    }
    fun isAppInstalled(packageName: String): Boolean {
        val packageManager = packageManager
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    fun hideBottomNavigation(){
        val bottomNavigationView: BottomNavigationView? = findViewById(R.id.bottom_navigation)
        if (bottomNavigationView != null) {
            bottomNavigationView.visibility = View.GONE
        }
    }

    fun showBottomNavigation() {
        val bottomNavigationView: BottomNavigationView? = findViewById(R.id.bottom_navigation)
        if (bottomNavigationView != null) {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }
    fun showToolbar() {
        val toolbar: RelativeLayout = findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE
    }
    fun hideToolbar() {
        val toolbar: RelativeLayout = findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
    }
    fun setAppTheme(currentTheme: String?) {
        when (currentTheme) {
            "LIGHT_THEME" -> setTheme(R.style.Theme_App_Light)
            else -> setTheme(R.style.Theme_App_Dark)
        }
    }
}