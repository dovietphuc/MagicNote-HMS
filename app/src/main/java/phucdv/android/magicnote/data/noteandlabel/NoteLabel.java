package phucdv.android.magicnote.data.noteandlabel;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "note_label",
        indices = {@Index(value = {"note_id", "label_id"}, unique = true)})
public class NoteLabel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long note_id;
    private long label_id;
    private String uid;
    private long time_stamp_update;
    private boolean enable;

    public NoteLabel(long note_id, long label_id, String uid, boolean enable) {
        this.id = Calendar.getInstance().getTimeInMillis();
        this.note_id = note_id;
        this.label_id = label_id;
        this.uid = uid;
        this.time_stamp_update = Calendar.getInstance().getTimeInMillis();
        this.enable = enable;
    }

    @Ignore
    public NoteLabel(){
        super();
        this.id = Calendar.getInstance().getTimeInMillis();
        this.time_stamp_update = Calendar.getInstance().getTimeInMillis();
        this.enable = true;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public long getLabel_id() {
        return label_id;
    }

    public void setLabel_id(long label_id) {
        this.label_id = label_id;
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
}
