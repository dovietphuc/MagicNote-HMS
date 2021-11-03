package phucdv.android.magicnote.sync;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;
import phucdv.android.magicnote.data.imageitem.ImageItem;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.noteandlabel.NoteLabel;
import phucdv.android.magicnote.data.textitem.TextItem;

public class BackUpWorker extends Worker {
    public static final String BACK_UP_WORKER_NAME = "magic_note.back_up_worker";

    public BackUpWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference(firebaseUser.getUid());

        NoteRoomDatabase noteRoomDatabase = NoteRoomDatabase.getDatabase(getApplicationContext());
        backupNote(userRef, noteRoomDatabase);
        backupTextItem(userRef, noteRoomDatabase);
        backupCheckbox(userRef, noteRoomDatabase);
        backupImageItem(userRef, noteRoomDatabase);
        backupLabel(userRef, noteRoomDatabase);
        backupNoteLabel(userRef, noteRoomDatabase);
        return Result.success();
    }

    public static void backupNote(DatabaseReference ref,  NoteRoomDatabase db){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Cursor cursor = db.query("SELECT * FROM note", null);
        while (cursor.moveToNext()){
            BackUpNoteItem note = new BackUpNoteItem(
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getLong(cursor.getColumnIndex("time_create")),
                    cursor.getLong(cursor.getColumnIndex("time_last_update")),
                    cursor.getInt(cursor.getColumnIndex("is_archive")) == 1,
                    cursor.getInt(cursor.getColumnIndex("is_deleted")) == 1,
                    cursor.getLong(cursor.getColumnIndex("order_in_parent")),
                    cursor.getInt(cursor.getColumnIndex("is_pinned")) == 1,
                    cursor.getInt(cursor.getColumnIndex("color")),
                    cursor.getInt(cursor.getColumnIndex("has_checkbox")) == 1,
                    cursor.getInt(cursor.getColumnIndex("has_image")) == 1,
                    cursor.getString(cursor.getColumnIndex("full_text")),
                    cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getString(cursor.getColumnIndex("user_name")),
                    cursor.getInt(cursor.getColumnIndex("enable")) == 1
            );
            note.setId(cursor.getLong(cursor.getColumnIndex("id")));

            ref.child("note/" + note.getId()).setValue(note);
        }
    }

    public static void backupTextItem(DatabaseReference ref,  NoteRoomDatabase db){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Cursor cursor = db.query("SELECT * FROM text_item", null);
        while (cursor.moveToNext()){
            TextItem item = new TextItem(
                    cursor.getLong(cursor.getColumnIndex("parent_id")),
                    cursor.getLong(cursor.getColumnIndex("order_in_parent")),
                    cursor.getString(cursor.getColumnIndex("content")),
                    cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getInt(cursor.getColumnIndex("enable")) == 1
            );
            item.setId(cursor.getLong(cursor.getColumnIndex("id")));
            item.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));

            ref.child("text_item/" + item.getId()).setValue(item);
        }
    }

    public static void backupCheckbox(DatabaseReference ref,  NoteRoomDatabase db){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Cursor cursor = db.query("SELECT * FROM checkbox_item", null);
        while (cursor.moveToNext()){
            CheckboxItem item = new CheckboxItem(
                    cursor.getLong(cursor.getColumnIndex("parent_id")),
                    cursor.getLong(cursor.getColumnIndex("order_in_parent")),
                    cursor.getInt(cursor.getColumnIndex("is_checked")) == 1,
                    cursor.getString(cursor.getColumnIndex("content")),
                    cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getInt(cursor.getColumnIndex("enable")) == 1
            );
            item.setId(cursor.getLong(cursor.getColumnIndex("id")));
            item.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));

            ref.child("checkbox_item/" + item.getId()).setValue(item);
        }
    }

    public static void backupImageItem(DatabaseReference ref,  NoteRoomDatabase db){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Cursor cursor = db.query("SELECT * FROM image_item", null);
        while (cursor.moveToNext()){
            ImageItem item = new ImageItem(
                    cursor.getLong(cursor.getColumnIndex("order_in_parent")),
                    cursor.getLong(cursor.getColumnIndex("parent_id")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getInt(cursor.getColumnIndex("enable")) == 1
            );
            item.setId(cursor.getLong(cursor.getColumnIndex("id")));
            item.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference(firebaseUser.getUid() + "/" + item.getPath());
            storageRef.putFile(Uri.fromFile(new File(item.getPath()))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.child("image_item/" + item.getId()).setValue(item);
                }
            });
        }
    }

    public static void backupLabel(DatabaseReference ref,  NoteRoomDatabase db){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Cursor cursor = db.query("SELECT * FROM label", null);
        while (cursor.moveToNext()){
            Label item = new Label(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getInt(cursor.getColumnIndex("enable")) == 1
            );
            item.setId(cursor.getLong(cursor.getColumnIndex("id")));
            item.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));

            ref.child("label/" + item.getId()).setValue(item);
        }
    }

    public static void backupNoteLabel(DatabaseReference ref,  NoteRoomDatabase db){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Cursor cursor = db.query("SELECT * FROM note_label", null);
        while (cursor.moveToNext()){
            NoteLabel item = new NoteLabel(
                    cursor.getLong(cursor.getColumnIndex("note_id")),
                    cursor.getLong(cursor.getColumnIndex("label_id")),
                    cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getInt(cursor.getColumnIndex("enable")) == 1
            );
            item.setId(cursor.getLong(cursor.getColumnIndex("id")));
            item.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));

            ref.child("note_label/" + item.getId()).setValue(item);
        }
    }
}
