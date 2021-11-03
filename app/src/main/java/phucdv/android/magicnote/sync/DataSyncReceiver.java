package phucdv.android.magicnote.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class DataSyncReceiver extends BroadcastReceiver {
    public static final String ACTION_SYNC_UP = "phucdv.android.action.SYNC_UP";
    public static final String ACTION_SYNC_DOWN = "phucdv.android.action.SYNC_DOWN";
    public static final String ACTION_SYNC = "phucdv.android.action.SYNC";
    public static final String ACTION_CANCEL_ALL = "phucdv.android.action.CANCEL_ALL";

    private WorkManager mWorkManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mWorkManager = WorkManager.getInstance(context.getApplicationContext());

        if(intent == null || intent.getAction() == null){
            return;
        }
        switch (intent.getAction()){
            case ACTION_SYNC_UP:
                WorkContinuation continuation =
                        mWorkManager.beginUniqueWork(BackUpWorker.BACK_UP_WORKER_NAME,
                                ExistingWorkPolicy.APPEND,
                                OneTimeWorkRequest.from(BackUpWorker.class));
                continuation.enqueue();
                break;
            case ACTION_SYNC_DOWN:
                continuation =
                        mWorkManager.beginUniqueWork(RestoreWorker.RESTORE_WORKER_NAME,
                                ExistingWorkPolicy.APPEND,
                                OneTimeWorkRequest.from(RestoreWorker.class));
                continuation.enqueue();
                break;
            case ACTION_SYNC:
                continuation =
                        mWorkManager.beginUniqueWork(AutoSyncWorker.AUTO_SYNC_WORKER_NAME,
                                ExistingWorkPolicy.APPEND,
                                OneTimeWorkRequest.from(AutoSyncWorker.class));
                continuation.enqueue();
                break;
            case ACTION_CANCEL_ALL:
                mWorkManager.cancelAllWork();
                break;
        }
    }
}
