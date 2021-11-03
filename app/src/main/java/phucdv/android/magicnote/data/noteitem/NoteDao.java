package phucdv.android.magicnote.data.noteitem;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;

@Dao
public interface NoteDao {

    @Query("SELECT * from note WHERE enable = 1")
    public LiveData<List<Note>> getNotes();

    @Query("SELECT * from note WHERE enable = 1 AND id = :id")
    public LiveData<Note> getNotesById(long id);

    @Query("SELECT * from note WHERE enable = 1 AND is_archive = 0 AND is_deleted = 0 ORDER BY is_pinned DESC, time_last_update DESC")
    public LiveData<List<Note>> getNotesInProcessing();

    @Query("SELECT * from note WHERE enable = 1 AND is_archive = 1 ORDER BY is_pinned DESC, time_last_update DESC")
    public LiveData<List<Note>> getNotesInArchive();

    @Query("SELECT * from note WHERE enable = 1 AND is_deleted = 1 ORDER BY is_pinned DESC, time_last_update DESC")
    public LiveData<List<Note>> getNotesInTrash();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public Long insert(Note note);

    @Query("UPDATE note SET enable = 0")
    public void deleteAll();

    @Query("UPDATE note SET enable = 0 WHERE is_deleted = 1")
    public void deleteAllTrash();

    @Query("UPDATE note SET enable = 0 WHERE id = :id")
    public void deleteNoteByID(long id);

    @Update(entity = Note.class)
    public void update(Note note);

    @Update(entity = Note.class)
    public void updateAll(List<Note> notes);
}
