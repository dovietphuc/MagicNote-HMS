package phucdv.android.magicnote.data.imageitem;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;

@Dao
public interface ImageItemDao {

    @Query("SELECT * FROM image_item WHERE enable = 1")
    public LiveData<List<ImageItem>> getAll();

    @Query("SELECT * FROM image_item WHERE enable = 1 AND parent_id = :parentId")
    public LiveData<List<ImageItem>> getImageItemsByParentId(long parentId);

    @Query("SELECT * FROM image_item WHERE enable = 1 AND id = :id")
    public LiveData<ImageItem> getImageItemById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public Long insert(ImageItem imageItem);

    @Query("UPDATE image_item SET enable = 0 WHERE id = :id")
    public void deleteById(long id);

    @Query("UPDATE image_item SET enable = 0 WHERE parent_id = :parentId")
    public void deleteByParentId(long parentId);

    @Update(entity = ImageItem.class)
    public void update(ImageItem imageItem);
}
