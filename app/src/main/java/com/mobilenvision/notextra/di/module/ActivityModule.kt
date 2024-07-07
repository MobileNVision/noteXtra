package com.mobilenvision.notextra.di.module


import androidx.core.util.Supplier
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.ViewModelProviderFactory
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.ui.base.BaseActivity
import com.mobilenvision.notextra.ui.login.LoginViewModel
import com.mobilenvision.notextra.ui.main.MainViewModel
import com.mobilenvision.notextra.ui.register.RegisterViewModel
import dagger.Module
import dagger.Provides


@Module
class ActivityModule(activity: BaseActivity<*, *>) {
    private val activity: BaseActivity<*, *>

    init {
        this.activity = activity
    }


    @Provides
    fun provideLoginViewModel(
        dataManager: DataManager
    ): LoginViewModel {
        val supplier: Supplier<LoginViewModel> =
            Supplier<LoginViewModel> {
                LoginViewModel(dataManager
                )
            }
        val factory: ViewModelProviderFactory<LoginViewModel> = ViewModelProviderFactory(
            LoginViewModel::class.java, supplier
        )
        return ViewModelProvider(activity,factory)[LoginViewModel::class.java]
    }
    @Provides
    fun provideRegisterViewModel(
        dataManager: DataManager
    ): RegisterViewModel {
        val supplier: Supplier<RegisterViewModel> =
            Supplier<RegisterViewModel> {
                RegisterViewModel(dataManager
                )
            }
        val factory: ViewModelProviderFactory<RegisterViewModel> = ViewModelProviderFactory(
            RegisterViewModel::class.java, supplier
        )
        return ViewModelProvider(activity,factory)[RegisterViewModel::class.java]
    }
    @Provides
    fun provideMainViewModel(
        dataManager: DataManager
    ): MainViewModel {
        val supplier: Supplier<MainViewModel> =
            Supplier<MainViewModel> {
                MainViewModel(dataManager
                )
            }
        val factory: ViewModelProviderFactory<MainViewModel> = ViewModelProviderFactory(
            MainViewModel::class.java, supplier
        )
        return ViewModelProvider(activity,factory)[MainViewModel::class.java]
    }
}