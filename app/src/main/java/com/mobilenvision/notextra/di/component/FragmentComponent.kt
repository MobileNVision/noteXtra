package com.mobilenvision.notextra.di.component


import com.mobilenvision.notextra.di.module.FragmentModule
import com.mobilenvision.notextra.di.scope.FragmentScope
import com.mobilenvision.notextra.ui.addDaily.AddDailyFragment
import com.mobilenvision.notextra.ui.addNote.AddNoteFragment
import com.mobilenvision.notextra.ui.daily.DailyFragment
import com.mobilenvision.notextra.ui.noteDetail.NoteDetailFragment
import com.mobilenvision.notextra.ui.notes.NotesFragment
import com.mobilenvision.notextra.ui.profile.ProfileFragment
import dagger.Component

@FragmentScope
@Component(modules = [FragmentModule::class], dependencies = [AppComponent::class])
interface FragmentComponent {
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: NotesFragment)
    fun inject(fragment: DailyFragment)
    fun inject(fragment: AddNoteFragment)
    fun inject(fragment: AddDailyFragment)
    fun inject(fragment: NoteDetailFragment)

}
