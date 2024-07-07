package com.mobilenvision.notextra


import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.google.firebase.FirebaseApp
import com.mobilenvision.notextra.di.component.AppComponent
import com.mobilenvision.notextra.di.component.DaggerAppComponent
import com.mobilenvision.notextra.ui.main.MainActivity

class NoteXtraApplication : Application() {
    var appComponent: AppComponent? = null


    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        appComponent!!.inject(this)
        FirebaseApp.initializeApp(this)

        val shortcutManager = getSystemService(ShortcutManager::class.java)
        val shortcut1 = ShortcutInfo.Builder(this, "add_note")
            .setShortLabel(getString(R.string.add_note))
            .setLongLabel(getString(R.string.add_note))
            .setIcon(Icon.createWithResource(this, R.drawable.notes))
            .setIntent(Intent(Intent.ACTION_VIEW, null, this, MainActivity::class.java))
            .build()

        val shortcut2 = ShortcutInfo.Builder(this, "add_reminder")
            .setShortLabel(getString(R.string.add_reminder))
            .setLongLabel(getString(R.string.add_reminder))
            .setIcon(Icon.createWithResource(this, R.drawable.reminder))
            .setIntent(Intent(Intent.ACTION_VIEW, null, this, MainActivity::class.java))
            .build()

        shortcutManager?.dynamicShortcuts = listOf(shortcut1, shortcut2)

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }
}
