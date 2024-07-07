package com.mobilenvision.notextra.di.component


import android.app.Application
import com.mobilenvision.notextra.NoteXtraApplication
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: NoteXtraApplication)

    fun getDataManager(): DataManager

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}
