package phucdv.android.magicnote.data.label;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import phucdv.android.magicnote.data.noteitem.Note;

@Dao
public interface LabelDao {
    @Query("SELECT * from label WHERE enable = 1")
    public LiveData<List<Label>> getAllLabels();

    @Query("SELECT * from label WHERE enable = 1 AND id = :id")
    public LiveData<Label> getLabelById(long id);

    @Query("SELECT * from label WHERE enable = 1 AND name like :name")
    public LiveData<Label> getLabelByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insert(Label label);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long[] insertAll(List<Label> labels);

    @Query("UPDATE label SET enable = 0 WHERE id = :labelId")
    public void delete(long labelId);
}