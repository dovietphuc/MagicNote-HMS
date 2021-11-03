package phucdv.android.magicnote.sync;

import java.util.Calendar;

public class BackUpNoteItem {
    private long id;
    private String title;
    private long time_create;
    private long time_last_update;
    private boolean is_archive;
    private boolean is_deleted;
    private long order_in_parent;
    private boolean is_pinned;
    private int color;
    private boolean has_checkbox;
    private boolean has_image;
    private String full_text;
    private String uid;
    private String user_name;
    private boolean enable;

    public BackUpNoteItem(String title, long time_create, long time_last_update, boolean is_archive,
                boolean is_deleted, long order_in_parent, boolean is_pinned, int color,
                boolean has_checkbox, boolean has_image, String full_text, String uid, String user_name, boolean enable) {
        this.title = title;
        this.time_create = time_create;
        this.time_last_update = time_last_update;
        this.is_archive = is_archive;
        this.is_deleted = is_deleted;
        this.order_in_parent = order_in_parent;
        this.is_pinned = is_pinned;
        this.color = color;
        this.has_checkbox = has_checkbox;
        this.has_image = has_image;
        this.full_text = full_text;
        this.uid = uid;
        this.user_name = user_name;
        this.enable = enable;
    }

    public BackUpNoteItem() {
        this.title = "";
        this.time_create = Calendar.getInstance().getTimeInMillis();
        this.time_last_update = Calendar.getInstance().getTimeInMillis();
        this.is_archive = false;
        this.is_deleted = false;
        this.order_in_parent = 0;
        this.is_pinned = false;
        this.color = 0;
        this.has_checkbox = false;
        this.has_image = false;
        this.full_text = "";
        this.uid = null;
        this.user_name = null;
        this.enable = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime_create() {
        return time_create;
    }

    public void setTime_create(long time_create) {
        this.time_create = time_create;
    }

    public long getTime_last_update() {
        return time_last_update;
    }

    public void setTime_last_update(long time_last_update) {
        this.time_last_update = time_last_update;
    }

    public boolean isIs_archive() {
        return is_archive;
    }

    public void setIs_archive(boolean is_archive) {
        this.is_archive = is_archive;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public long getOrder_in_parent() {
        return order_in_parent;
    }

    public void setOrder_in_parent(long order_in_parent) {
        this.order_in_parent = order_in_parent;
    }

    public boolean isIs_pinned() {
        return is_pinned;
    }

    public void setIs_pinned(boolean is_pinned) {
        this.is_pinned = is_pinned;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isHas_checkbox() {
        return has_checkbox;
    }

    public void setHas_checkbox(boolean has_checkbox) {
        this.has_checkbox = has_checkbox;
    }

    public boolean isHas_image() {
        return has_image;
    }

    public void setHas_image(boolean has_image) {
        this.has_image = has_image;
    }

    public String getFull_text(){
        return full_text;
    }

    public void setFull_text(String full_text){
        this.full_text = full_text;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
