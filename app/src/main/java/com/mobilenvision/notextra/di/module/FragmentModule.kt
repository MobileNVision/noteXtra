package com.mobilenvision.notextra.di.module

import androidx.core.util.Supplier
import androidx.lifecycle.ViewModelProvider
import com.mobilenvision.notextra.ViewModelProviderFactory
import com.mobilenvision.notextra.data.DataManager
import com.mobilenvision.notextra.ui.addNote.AddNoteViewModel
import com.mobilenvision.notextra.ui.base.BaseFragment
import com.mobilenvision.notextra.ui.noteDetail.NoteDetailViewModel
import com.mobilenvision.notextra.ui.notes.NotesAdapter
import com.mobilenvision.notextra.ui.notes.NotesViewModel
import com.mobilenvision.notextra.ui.profile.ProfileViewModel
import dagger.Module
import dagger.Provides


@Module
class FragmentModule(fragment: BaseFragment<*, *>) {
    private val fragment: BaseFragment<*, *>

    init {
        this.fragment = fragment
    }
    @Provides
    fun provideProfileViewModel(
        dataManager: DataManager
    ): ProfileViewModel {
        val supplier: Supplier<ProfileViewModel> =
            Supplier<ProfileViewModel> {
                ProfileViewModel(dataManager)
            }
        val factory: ViewModelProviderFactory<ProfileViewModel> = ViewModelProviderFactory(
            ProfileViewModel::class.java, supplier
        )
        return ViewModelProvider(fragment, factory)[ProfileViewModel::class.java]
    }
    @Provides
    fun provideNotesViewModel(
        dataManager: DataManager
    ): NotesViewModel {
        val supplier: Supplier<NotesViewModel> =
            Supplier<NotesViewModel> {
                NotesViewModel(dataManager)
            }
        val factory: ViewModelProviderFactory<NotesViewModel> = ViewModelProviderFactory(
            NotesViewModel::class.java, supplier
        )
        return ViewModelProvider(fragment, factory)[NotesViewModel::class.java]
    }
    @Provides
    fun provideAddNoteViewModel(
        dataManager: DataManager
    ): AddNoteViewModel {
        val supplier: Supplier<AddNoteViewModel> =
            Supplier<AddNoteViewModel> {
                AddNoteViewModel(dataManager)
            }
        val factory: ViewModelProviderFactory<AddNoteViewModel> = ViewModelProviderFactory(
            AddNoteViewModel::class.java, supplier
        )
        return ViewModelProvider(fragment, factory)[AddNoteViewModel::class.java]
    }
    @Provides
    fun provideNoteDetailViewModel(
        dataManager: DataManager
    ): NoteDetailViewModel {
        val supplier: Supplier<NoteDetailViewModel> =
            Supplier<NoteDetailViewModel> {
                NoteDetailViewModel(dataManager)
            }
        val factory: ViewModelProviderFactory<NoteDetailViewModel> = ViewModelProviderFactory(
            NoteDetailViewModel::class.java, supplier
        )
        return ViewModelProvider(fragment, factory)[NoteDetailViewModel::class.java]
    }
    @Provides
    fun provideNotesAdapter(): NotesAdapter? {
        return NotesAdapter(ArrayList())
    }
}
