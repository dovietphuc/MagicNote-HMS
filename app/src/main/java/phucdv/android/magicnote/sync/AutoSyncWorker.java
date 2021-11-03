package phucdv.android.magicnote.sync;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import phucdv.android.magicnote.data.Converters;
import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItem;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItemDao;
import phucdv.android.magicnote.data.imageitem.ImageItem;
import phucdv.android.magicnote.data.imageitem.ImageItemDao;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.label.LabelDao;
import phucdv.android.magicnote.data.noteandlabel.NoteLabel;
import phucdv.android.magicnote.data.noteandlabel.NoteLabelDao;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.data.noteitem.NoteDao;
import phucdv.android.magicnote.data.textitem.TextItem;
import phucdv.android.magicnote.data.textitem.TextItemDao;
import phucdv.android.magicnote.util.FileHelper;

public class AutoSyncWorker extends Worker {
    public static final String AUTO_SYNC_WORKER_NAME = "magic_note.auto_sync_worker";
    public static final long SCHEDULE_TIME = 15;

    private int mNumOfChild = 6;
    private int mNumOfChildCpl = 0;
    public AutoSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference(firebaseUser.getUid());

        restoreNote(firebaseDatabase, firebaseUser);
        restoreTextItem(firebaseDatabase, firebaseUser);
        restoreCheckbox(firebaseDatabase, firebaseUser);
        restoreImage(firebaseDatabase, firebaseUser);
        restoreLabel(firebaseDatabase, firebaseUser);
        restoreNoteLabel(firebaseDatabase, firebaseUser);

        return Result.success();
    }

    public void backup(FirebaseDatabase firebaseDatabase, FirebaseUser user){
        NoteRoomDatabase noteRoomDatabase = NoteRoomDatabase.getDatabase(getApplicationContext());

        DatabaseReference userRef = firebaseDatabase.getReference(user.getUid());

        BackUpWorker.backupNote(userRef, noteRoomDatabase);
        BackUpWorker.backupTextItem(userRef, noteRoomDatabase);
        BackUpWorker.backupCheckbox(userRef, noteRoomDatabase);
        BackUpWorker.backupImageItem(userRef, noteRoomDatabase);
        BackUpWorker.backupLabel(userRef, noteRoomDatabase);
        BackUpWorker.backupNoteLabel(userRef, noteRoomDatabase);
    }

    public void restoreNote(FirebaseDatabase firebaseDatabase, FirebaseUser user){
        DatabaseReference noteRef = firebaseDatabase.getReference(user.getUid() + "/note");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NoteRoomDatabase db = NoteRoomDatabase.getDatabase(getApplicationContext());
                NoteDao noteDao = db.noteDao();
                for(DataSnapshot child : snapshot.getChildren()){
                    BackUpNoteItem backUpNote = child.getValue(BackUpNoteItem.class);
                    Cursor cursor = db.query("SELECT * FROM note WHERE id=" + backUpNote.getId(), null);
                    if(cursor.moveToFirst()){
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
                                cursor.getInt(cursor.getColumnIndex("enable")) == 1);
                        if(note.getTime_last_update() >= backUpNote.getTime_last_update()
                                && note.getColor() == backUpNote.getColor()
                                && note.isIs_pinned() == backUpNote.isIs_pinned()
                                && note.isIs_archive() == backUpNote.isIs_archive()
                                && note.isIs_deleted() == backUpNote.isIs_deleted()
                                && !note.isEnable()){
                            continue;
                        }
                    }
                    Note note = new Note(backUpNote.getTitle(), Converters.datestampToCalendar(backUpNote.getTime_create()),
                            Converters.datestampToCalendar(backUpNote.getTime_last_update()), backUpNote.isIs_archive(), backUpNote.isIs_deleted(),
                            backUpNote.getOrder_in_parent(), backUpNote.isIs_pinned(), backUpNote.getColor(),
                            backUpNote.isHas_checkbox(), backUpNote.isHas_image(), backUpNote.getFull_text(), backUpNote.getUid(), backUpNote.getUser_name(), backUpNote.isEnable());
                    note.setId(backUpNote.getId());
                    noteDao.insert(note);
                }

                mNumOfChildCpl++;
                if(mNumOfChildCpl == mNumOfChild){
                    backup(firebaseDatabase, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void restoreTextItem(FirebaseDatabase firebaseDatabase, FirebaseUser user){
        DatabaseReference noteRef = firebaseDatabase.getReference(user.getUid() + "/text_item");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NoteRoomDatabase db = NoteRoomDatabase.getDatabase(getApplicationContext());
                TextItemDao dao = db.textItemDao();
                for(DataSnapshot child : snapshot.getChildren()){
                    TextItem item = child.getValue(TextItem.class);
                    Cursor cursor = db.query("SELECT * FROM text_item WHERE id=" + item.getId(), null);
                    if(cursor.moveToFirst()){
                        TextItem lastUpdate = new TextItem(
                                cursor.getLong(cursor.getColumnIndex("parent_id")),
                                cursor.getLong(cursor.getColumnIndex("order_in_parent")),
                                cursor.getString(cursor.getColumnIndex("content")),
                                cursor.getString(cursor.getColumnIndex("uid")),
                                cursor.getInt(cursor.getColumnIndex("enable")) == 1
                        );
                        lastUpdate.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        lastUpdate.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));

                        if(lastUpdate.getTime_stamp_update() >= item.getTime_stamp_update()
                            && !lastUpdate.isEnable()
                            && lastUpdate.getOrder_in_parent() == item.getOrder_in_parent()){
                            continue;
                        }
                    }
                    dao.insert(item);
                }
                mNumOfChildCpl++;
                if(mNumOfChildCpl == mNumOfChild){
                    backup(firebaseDatabase, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void restoreCheckbox(FirebaseDatabase firebaseDatabase, FirebaseUser user){
        DatabaseReference noteRef = firebaseDatabase.getReference(user.getUid() + "/checkbox_item");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NoteRoomDatabase db = NoteRoomDatabase.getDatabase(getApplicationContext());
                CheckboxItemDao dao = db.checkboxItemDao();
                for(DataSnapshot child : snapshot.getChildren()){
                    CheckboxItem item = child.getValue(CheckboxItem.class);
                    Cursor cursor = db.query("SELECT * FROM checkbox_item WHERE id=" + item.getId(), null);
                    if(cursor.moveToFirst()){
                        CheckboxItem lastUpdate = new CheckboxItem(
                                cursor.getLong(cursor.getColumnIndex("parent_id")),
                                cursor.getLong(cursor.getColumnIndex("order_in_parent")),
                                cursor.getInt(cursor.getColumnIndex("is_checked")) == 1,
                                cursor.getString(cursor.getColumnIndex("content")),
                                cursor.getString(cursor.getColumnIndex("uid")),
                                cursor.getInt(cursor.getColumnIndex("enable")) == 1
                        );
                        lastUpdate.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        lastUpdate.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));
                        if(lastUpdate.getTime_stamp_update() >= item.getTime_stamp_update()
                            && !lastUpdate.isEnable()
                                && lastUpdate.getOrder_in_parent() == item.getOrder_in_parent()){
                            continue;
                        }
                    }
                    dao.insert(item);
                }
                mNumOfChildCpl++;
                if(mNumOfChildCpl == mNumOfChild){
                    backup(firebaseDatabase, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void restoreImage(FirebaseDatabase firebaseDatabase, FirebaseUser user){
        DatabaseReference noteRef = firebaseDatabase.getReference(user.getUid() + "/image_item");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NoteRoomDatabase db = NoteRoomDatabase.getDatabase(getApplicationContext());
                ImageItemDao dao = db.imageItemDao();
                for(DataSnapshot child : snapshot.getChildren()){
                    ImageItem item = child.getValue(ImageItem.class);
                    Cursor cursor = db.query("SELECT * FROM image_item WHERE id=" + item.getId(), null);
                    if(cursor.moveToFirst()){
                        ImageItem lastUpdate = new ImageItem(
                                cursor.getLong(cursor.getColumnIndex("order_in_parent")),
                                cursor.getLong(cursor.getColumnIndex("parent_id")),
                                cursor.getString(cursor.getColumnIndex("path")),
                                cursor.getString(cursor.getColumnIndex("uid")),
                                cursor.getInt(cursor.getColumnIndex("enable")) == 1
                        );
                        lastUpdate.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        lastUpdate.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));
                        if(lastUpdate.getTime_stamp_update() >= item.getTime_stamp_update()
                                && !lastUpdate.isEnable()
                                && lastUpdate.getOrder_in_parent() == item.getOrder_in_parent()){
                            continue;
                        }
                    }

                    tryRestoreImage(firebaseDatabase, user, item, dao);
                }

                mNumOfChildCpl++;
                if(mNumOfChildCpl == mNumOfChild){
                    backup(firebaseDatabase, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void tryRestoreImage(FirebaseDatabase firebaseDatabase, FirebaseUser user, ImageItem item, ImageItemDao dao){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference(user.getUid() + "/" + item.getPath());
        if(item.getPath().contains("handDrawer")){
            FileHelper.mkdir(FileHelper.handDrawDir(getApplicationContext()));
        } else {
            FileHelper.mkdir(FileHelper.photoDir(getApplicationContext()));
        }
        storageRef.getFile(new File(item.getPath())).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    dao.insert(item);
                } else {
                    tryRestoreImage(firebaseDatabase, user, item, dao);
                }
            }
        });
    }

    public void restoreLabel(FirebaseDatabase firebaseDatabase, FirebaseUser user){
        DatabaseReference noteRef = firebaseDatabase.getReference(user.getUid() + "/label");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NoteRoomDatabase db = NoteRoomDatabase.getDatabase(getApplicationContext());
                LabelDao dao = db.labelDao();
                for(DataSnapshot child : snapshot.getChildren()){
                    Label item = child.getValue(Label.class);
                    Cursor cursor = db.query("SELECT * FROM label WHERE id=" + item.getId(), null);
                    if(cursor.moveToFirst()){
                        Label lastUpdate = new Label(
                                cursor.getString(cursor.getColumnIndex("name")),
                                cursor.getString(cursor.getColumnIndex("uid")),
                                cursor.getInt(cursor.getColumnIndex("enable")) == 1
                        );
                        lastUpdate.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        lastUpdate.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));
                        if(lastUpdate.getTime_stamp_update() >= item.getTime_stamp_update()
                            && !lastUpdate.isEnable()
                            && lastUpdate.getName() == item.getName()){
                            continue;
                        }
                    }
                    dao.insert(item);
                }
                mNumOfChildCpl++;
                if(mNumOfChildCpl == mNumOfChild){
                    backup(firebaseDatabase, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void restoreNoteLabel(FirebaseDatabase firebaseDatabase, FirebaseUser user){
        DatabaseReference noteRef = firebaseDatabase.getReference(user.getUid() + "/note_label");
        noteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                NoteRoomDatabase db = NoteRoomDatabase.getDatabase(getApplicationContext());
                NoteLabelDao dao = db.noteLabelDao();
                for(DataSnapshot child : snapshot.getChildren()){
                    NoteLabel item = child.getValue(NoteLabel.class);
                    Cursor cursor = db.query("SELECT * FROM note_label WHERE id=" + item.getId(), null);
                    if(cursor.moveToFirst()){
                        NoteLabel lastUpdate = new NoteLabel(
                                cursor.getLong(cursor.getColumnIndex("note_id")),
                                cursor.getLong(cursor.getColumnIndex("label_id")),
                                cursor.getString(cursor.getColumnIndex("uid")),
                                cursor.getInt(cursor.getColumnIndex("enable")) == 1
                        );
                        lastUpdate.setId(cursor.getLong(cursor.getColumnIndex("id")));
                        lastUpdate.setTime_stamp_update(cursor.getLong(cursor.getColumnIndex("time_stamp_update")));
                        if(lastUpdate.getTime_stamp_update() >= item.getTime_stamp_update()
                            && !lastUpdate.isEnable()){
                            continue;
                        }
                    }
                    dao.insert(item);
                }
                mNumOfChildCpl++;
                if(mNumOfChildCpl == mNumOfChild){
                    backup(firebaseDatabase, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
