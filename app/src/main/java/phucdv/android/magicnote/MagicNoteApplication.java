package phucdv.android.magicnote;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.sync.DataSyncReceiver;

public class MagicNoteApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            Intent syncIntent = new Intent();
            syncIntent.setAction(DataSyncReceiver.ACTION_SYNC_DOWN);
            syncIntent.setComponent(new ComponentName(this, DataSyncReceiver.class));
            sendBroadcast(syncIntent);
        }

        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(this);
        db.noteDao().getNotes().observeForever(items -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null) {
                Intent syncIntent = new Intent();
                syncIntent.setAction(DataSyncReceiver.ACTION_SYNC_UP);
                syncIntent.setComponent(new ComponentName(this, DataSyncReceiver.class));
                sendBroadcast(syncIntent);
            }
        });

        db.textItemDao().getAll().observeForever(items -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null) {
                Intent syncIntent = new Intent();
                syncIntent.setAction(DataSyncReceiver.ACTION_SYNC_UP);
                syncIntent.setComponent(new ComponentName(this, DataSyncReceiver.class));
                sendBroadcast(syncIntent);
            }
        });

        db.checkboxItemDao().getAll().observeForever(items -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null) {
                Intent syncIntent = new Intent();
                syncIntent.setAction(DataSyncReceiver.ACTION_SYNC_UP);
                syncIntent.setComponent(new ComponentName(this, DataSyncReceiver.class));
                sendBroadcast(syncIntent);
            }
        });

        db.imageItemDao().getAll().observeForever(items -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null) {
                Intent syncIntent = new Intent();
                syncIntent.setAction(DataSyncReceiver.ACTION_SYNC_UP);
                syncIntent.setComponent(new ComponentName(this, DataSyncReceiver.class));
                sendBroadcast(syncIntent);
            }
        });

        db.labelDao().getAllLabels().observeForever(items -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null) {
                Intent syncIntent = new Intent();
                syncIntent.setAction(DataSyncReceiver.ACTION_SYNC_UP);
                syncIntent.setComponent(new ComponentName(this, DataSyncReceiver.class));
                sendBroadcast(syncIntent);
            }
        });

        db.noteLabelDao().getAlls().observeForever(items -> {
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null) {
                Intent syncIntent = new Intent();
                syncIntent.setAction(DataSyncReceiver.ACTION_SYNC_UP);
                syncIntent.setComponent(new ComponentName(this, DataSyncReceiver.class));
                sendBroadcast(syncIntent);
            }
        });
    }
}
