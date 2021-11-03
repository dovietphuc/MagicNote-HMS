package phucdv.android.magicnote.data.noteandlabel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.noteitem.Note;

@Dao
public interface NoteLabelDao {

    @Query("SELECT * FROM note_label WHERE enable = 1")
    public LiveData<List<NoteLabel>> getAlls();

    @Query("SELECT * FROM note INNER JOIN note_label ON note.id = note_label.note_id WHERE note.enable = 1 AND note_label.enable = 1 AND label_id = :labelId")
    public LiveData<List<Note>> getNotesByLabelId(long labelId);

    @Query("SELECT * FROM label INNER JOIN note_label ON label.id = note_label.label_id WHERE label.enable = 1 AND note_label.enable = 1 AND note_id = :noteId")
    public LiveData<List<Label>> getLabelsByNoteId(long noteId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insert(NoteLabel noteLabel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long[] insertAll(List<NoteLabel> noteLabels);

    @Query("UPDATE note_label SET enable = 0 WHERE id = :noteLabelId")
    public void delete(long noteLabelId);

    @Query("UPDATE note_label SET enable = 0 WHERE note_id = :note_id")
    public void deleteForNote(long note_id);

    @Query("UPDATE label SET enable = 0 WHERE id NOT IN (SELECT label_id FROM note_label)")
    public void deleteLabelIfNeed();
}
