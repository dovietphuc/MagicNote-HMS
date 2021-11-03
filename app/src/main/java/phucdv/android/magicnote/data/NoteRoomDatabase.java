package phucdv.android.magicnote.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

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
import phucdv.android.magicnote.sync.DataSyncReceiver;
import phucdv.android.magicnote.util.Constants;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@Database(entities = {
        Note.class,
        TextItem.class,
        CheckboxItem.class,
        ImageItem.class,
        Label.class,
        NoteLabel.class
}, version = 6,  exportSchema = false)
@TypeConverters(Converters.class)
public abstract class NoteRoomDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public abstract TextItemDao textItemDao();
    public abstract CheckboxItemDao checkboxItemDao();
    public abstract ImageItemDao imageItemDao();
    public abstract LabelDao labelDao();
    public abstract NoteLabelDao noteLabelDao();
    private static NoteRoomDatabase INSTANCE;

    public static NoteRoomDatabase getDatabase(Context context){
        if(INSTANCE == null){
            synchronized (NoteRoomDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteRoomDatabase.class, Constants.DB_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Bkav PhucDVb: TODO: DO SOMETHING WHEN DATABASE OPEN
        }
    };
}
