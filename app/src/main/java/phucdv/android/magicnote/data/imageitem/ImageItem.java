package phucdv.android.magicnote.data.imageitem;

import androidx.room.Entity;
import androidx.room.Ignore;

import phucdv.android.magicnote.data.BaseItem;

@Entity(tableName = "image_item")
public class ImageItem extends BaseItem {
    private String path;
    public ImageItem(long order_in_parent, long parent_id, String path, String uid, boolean enable) {
        super(order_in_parent, parent_id, uid, enable);
        this.path = path;
    }

    @Ignore
    public ImageItem(){
        super();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
