package phucdv.android.magicnote.data.textitem;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import phucdv.android.magicnote.data.BaseItem;

@Entity(tableName = "text_item")
public class TextItem extends BaseItem {
    private String content;

    @Ignore
    public TextItem(){
        super();
    }

    public TextItem(long parent_id, long order_in_parent, String content, String uid, boolean enable) {
        super(order_in_parent, parent_id, uid, enable);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
