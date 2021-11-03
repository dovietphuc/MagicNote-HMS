package com.phucdvb.drawer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserHandle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.phucdvb.drawer.view.DrawerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class DrawerActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ACTION_HAND_DRAWER = "phucdv.intent.action.HAND_DRAWER";
    public static final String EXTRA_HAND_DRAWER = "phucdv.intent.extra.HAND_DRAWER";

    DrawerView mDrawerView;
    ImageButton mBtnClear;
    ImageButton mBtnUndo;
    ImageButton mBtnRedo;
    ImageButton mBtnCancel;
    ImageButton mBtnDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        mDrawerView = findViewById(R.id.drawer);
        mBtnClear = findViewById(R.id.btn_clear);
        mBtnUndo = findViewById(R.id.btn_undo);
        mBtnRedo = findViewById(R.id.btn_redo);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mBtnDone = findViewById(R.id.btn_done);

        mBtnClear.setOnClickListener(this);
        mBtnUndo.setOnClickListener(this);
        mBtnRedo.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_clear) {
            mDrawerView.clearAll();
        } else if (id == R.id.btn_undo) {
            mDrawerView.undo();
        } else if (id == R.id.btn_redo) {
            mDrawerView.redo();
        } else if (id == R.id.btn_cancel) {
            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.confirm_cancel)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (id == R.id.btn_done) {
            new AsyncTask<Void, Void, Intent>(){

                @Override
                protected Intent doInBackground(Void... voids) {
                    Bitmap bitmap = viewToBitmap(mDrawerView);
                    String filename = String.valueOf(new Date().getTime()) + ".png";
                    String dir;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                        dir = getExternalFilesDir(Environment.DIRECTORY_DCIM)
                                + File.separator + "MagicNote"
                                + File.separator + "handDrawer";
                    }
                    else
                    {
                        dir = Environment.getExternalStorageDirectory().toString()
                                + File.separator + "MagicNote"
                                + File.separator + "handDrawer";
                    }
                    String path = dir + File.separator + filename;
                    mkdir(dir);
                    saveBitmapToFile(bitmap, path);
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_HAND_DRAWER, path);
                    return intent;
                }

                @Override
                protected void onPostExecute(Intent intent) {
                    setResult(RESULT_OK, intent);
                    finish();
                    super.onPostExecute(intent);
                }
            }.execute();
        }
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void saveBitmapToFile(Bitmap bmp, String path){
        try (FileOutputStream out = new FileOutputStream(path)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mkdir(String dir){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.createDirectories(Paths.get(dir));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}