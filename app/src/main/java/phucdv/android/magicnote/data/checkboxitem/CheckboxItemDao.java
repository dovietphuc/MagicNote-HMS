package phucdv.android.magicnote.data.checkboxitem;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import phucdv.android.magicnote.data.textitem.TextItem;

@Dao
public interface CheckboxItemDao {

    @Query("SELECT * FROM checkbox_item WHERE enable = 1")
    public LiveData<List<CheckboxItem>> getAll();

    @Query("SELECT * FROM checkbox_item WHERE enable = 1 AND id = :id")
    public LiveData<List<CheckboxItem>> getCheckboxItemForId(long id);

    @Query("SELECT * FROM checkbox_item WHERE enable = 1 AND parent_id = :parentId")
    public LiveData<List<CheckboxItem>> getCheckboxItemForParentId(long parentId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insert(CheckboxItem checkboxItem);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public Long[] insertAll(List<CheckboxItem> checkboxItems);

    @Query("UPDATE checkbox_item SET enable = 0 WHERE id = :id")
    public void deleteById(long id);

    @Query("UPDATE checkbox_item SET enable = 0 WHERE parent_id = :parentId")
    public void deleteByParentId(long parentId);

    @Update(entity = CheckboxItem.class)
    public void update(CheckboxItem checkboxItem);

    @Update(entity = CheckboxItem.class)
    public void updateAll(List<CheckboxItem> checkboxItems);
}
