package com.mobilenvision.notextra.di.component

import com.mobilenvision.notextra.di.module.ActivityModule
import com.mobilenvision.notextra.di.scope.ActivityScope
import com.mobilenvision.notextra.ui.login.LoginActivity
import com.mobilenvision.notextra.ui.main.MainActivity
import com.mobilenvision.notextra.ui.register.RegisterActivity
import dagger.Component


@ActivityScope
@Component(modules = [ActivityModule::class], dependencies = [AppComponent::class])
interface ActivityComponent {
    fun inject(activity: LoginActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: RegisterActivity)
}
