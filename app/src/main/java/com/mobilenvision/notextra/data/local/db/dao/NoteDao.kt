package com.mobilenvision.notextra.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mobilenvision.notextra.data.model.db.Note

@Dao
interface NoteDao {
    @Delete
    fun deleteNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun loadNoteById(noteId: String): Note

    @Query("SELECT * FROM notes WHERE isSynchronized = 'false'")
    fun getUnSynchronizedNotes(): List<Note>

    @Query("UPDATE notes SET isSynchronized = :isSynchronized WHERE id = :noteId")
    fun updateIsSynchronized(noteId: String, isSynchronized: Boolean)

    @Query("UPDATE notes SET category = :categoryName WHERE id = :noteId")
    fun updateCategory(noteId: String, categoryName: String?)

}
