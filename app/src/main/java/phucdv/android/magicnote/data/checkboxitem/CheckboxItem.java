package phucdv.android.magicnote.data.checkboxitem;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import phucdv.android.magicnote.data.BaseItem;

@Entity(tableName = "checkbox_item")
public class CheckboxItem extends BaseItem {
    private boolean is_checked;
    private String content;

    public CheckboxItem(long parent_id, long order_in_parent, boolean is_checked, String content, String uid, boolean enable) {
        super(order_in_parent, parent_id, uid, enable);
        this.is_checked = is_checked;
        this.content = content;
    }

    @Ignore
    public CheckboxItem(){
        super();
    }

    public boolean isIs_checked() {
        return is_checked;
    }

    public void setIs_checked(boolean is_checked) {
        this.is_checked = is_checked;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
