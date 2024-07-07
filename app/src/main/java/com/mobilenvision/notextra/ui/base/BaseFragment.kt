package com.mobilenvision.notextra.ui.base


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.mobilenvision.notextra.NoteXtraApplication
import com.mobilenvision.notextra.di.component.DaggerFragmentComponent
import com.mobilenvision.notextra.di.component.FragmentComponent
import com.mobilenvision.notextra.di.module.FragmentModule
import javax.inject.Inject

abstract class BaseFragment<T : ViewDataBinding?, V : BaseViewModel<*>> : Fragment(){
    @Inject
    protected lateinit var mViewModel: V
    var baseActivity: BaseActivity<*,*>? = null
        private set
    private var mRootView: View? = null
    private var viewDataBinding: T? = null


    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            baseActivity = context
            context.onFragmentAttached()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDependencyInjection(getBuildComponent())
        setHasOptionsMenu(false)
    }
    abstract fun performDependencyInjection(buildComponent: FragmentComponent)
    private fun getBuildComponent(): FragmentComponent {
        return DaggerFragmentComponent.builder()
            .appComponent((context?.applicationContext as NoteXtraApplication).appComponent)
            .fragmentModule(FragmentModule(this))
            .build()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = viewDataBinding!!.root
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.lifecycleOwner = this
        viewDataBinding!!.executePendingBindings()
    }


    interface Callback {
        fun onFragmentAttached()
        fun onFragmentDetached(tag: String?)
    }

    open fun fetchViewDataBinding(): T {
        return viewDataBinding!!
    }
    open fun hideBottomNavigation() {
        baseActivity?.hideBottomNavigation()

    }
    open fun showBottomNavigation() {
        baseActivity?.showBottomNavigation()
    }
    open fun hideToolbar() {
        baseActivity?.hideToolbar()

    }
    open fun setAppTheme(currentTheme: String){
        baseActivity?.setAppTheme(currentTheme)
    }
    open fun showToolbar() {
        baseActivity?.showToolbar()
    }
    open fun showToast(message: String) {
        baseActivity?.showToastMessage(message)
    }
    open fun loadFragment(fragment: Fragment, fragmentTag: String) {
    baseActivity?.loadFragment(fragment,fragmentTag)
    }
    }
