package phucdv.android.magicnote.sync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.authentic.LoginActivity;

public class BackUpActivity extends AppCompatActivity {

    public static final String AUTO_BACKUP="auto_backup";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private FirebaseUser firebaseUser;

    private WorkManager mWorkManager;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up);

        mWorkManager = WorkManager.getInstance(getApplication());

        SwitchCompat switchAutoBackup = findViewById(R.id.switch_auto_backup);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        boolean autoBackup=sharedPreferences.getBoolean(AUTO_BACKUP, false);

        if (autoBackup){
            switchAutoBackup.setChecked(true);
        }else {
            switchAutoBackup.setChecked(false);
        }

        switchAutoBackup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor = sharedPreferences.edit();
                    editor.putBoolean(AUTO_BACKUP,true);
                    editor.apply();
                    startAutoSync();
                }else {
                    editor = sharedPreferences.edit();
                    editor.putBoolean(AUTO_BACKUP,false);
                    editor.apply();
                    cancelAutoSync();
                }
            }
        });
    }

    public void startAutoSync(){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(AutoSyncWorker.class,
                        AutoSyncWorker.SCHEDULE_TIME,
                        TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();
        mWorkManager.enqueueUniquePeriodicWork(AutoSyncWorker.AUTO_SYNC_WORKER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request);
    }

    public void cancelAutoSync(){
        mWorkManager.cancelUniqueWork(AutoSyncWorker.AUTO_SYNC_WORKER_NAME);
    }

    public void backUpNote(View view) {
        WorkContinuation continuation =
                mWorkManager.beginUniqueWork(BackUpWorker.BACK_UP_WORKER_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(BackUpWorker.class));
        continuation.enqueue();
    }

    public void restoreNote(View view) {
        WorkContinuation continuation =
                mWorkManager.beginUniqueWork(RestoreWorker.RESTORE_WORKER_NAME,
                        ExistingWorkPolicy.REPLACE,
                        OneTimeWorkRequest.from(RestoreWorker.class));
        continuation.enqueue();
    }

    public void logout(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this,LoginActivity.class));
    }
}
