package phucdv.android.magicnote.data.label;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "label",
indices = {@Index(value = {"name"}, unique = true)})
public class Label {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String uid;
    private long time_stamp_update;
    private boolean enable;

    public Label(String name, String uid, boolean enable) {
        this.id = Calendar.getInstance().getTimeInMillis();
        this.name = name;
        this.uid = uid;
        this.time_stamp_update = Calendar.getInstance().getTimeInMillis();
        this.enable = enable;
    }

    @Ignore
    public Label(){
        super();
        this.id = Calendar.getInstance().getTimeInMillis();
        this.time_stamp_update = Calendar.getInstance().getTimeInMillis();
        this.enable = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTime_stamp_update() {
        return time_stamp_update;
    }

    public void setTime_stamp_update(long time_stamp_update) {
        this.time_stamp_update = time_stamp_update;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
