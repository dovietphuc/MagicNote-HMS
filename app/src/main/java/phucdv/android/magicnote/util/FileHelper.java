package phucdv.android.magicnote.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import phucdv.android.magicnote.noteinterface.AsyncResponse;

public class FileHelper {
    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static void saveBitmapToFile(Bitmap bitmap, String filePath){
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mkdir(String dir){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.createDirectories(Paths.get(dir));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String photoDir(Context context){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                    + File.separator + "MagicNote"
                    + File.separator + "photo";
//        }
//        else
//        {
//            return Environment.getExternalStorageDirectory().toString()
//                    + File.separator + "MagicNote"
//                    + File.separator + "photo";
//        }
    }

    public static String handDrawDir(Context context){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return context.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                    + File.separator + "MagicNote"
                    + File.separator + "handDrawer";
//        }
//        else
//        {
//            return Environment.getExternalStorageDirectory().toString()
//                    + File.separator + "MagicNote"
//                    + File.separator + "handDrawer";
//        }
    }

    public static String createFileName(String ext){
        return new Date().getTime() + "." + ext;
    }

    public static File createFile(String folder, String fileName){
        mkdir(folder);
        return new File(folder + File.separator + fileName);
    }

    public static File getFileFromUri(Context context, Uri uri){
        String localPath =  FileHelper.getPhysicalLocation(context, uri);
        return new File(localPath);
    }

    public static String getPhysicalLocation(Context context, Uri uri){
        Cursor cursor =
                context.getContentResolver().query(uri,
                        new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
        if(cursor != null){
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
            if(cursor.moveToFirst()){
                return cursor.getString(dataIndex);
            }
        }
        return null;
    }

    public static boolean deleteFile(Context context, String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
            if(file.exists()){
                try {
                    file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(file.exists()){
                    return context.getApplicationContext().deleteFile(file.getName());
                }
                return true;
            }
            return true;
        }
        return false;
    }

    public static class deleteFileAsyncTask extends AsyncTask<String, Void, Boolean> {

        private Context mContext;
        private AsyncResponse mResponse;

        public deleteFileAsyncTask(Context context, AsyncResponse response) {
            mContext = context;
            mResponse = response;
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            return FileHelper.deleteFile(mContext, params[0]);
        }

        @Override
        protected void onPostExecute(Boolean unused) {
            mResponse.processFinish(unused);
            super.onPostExecute(unused);
        }
    }

    public static void deleteFileSyncTask(Context context, AsyncResponse response, String path){
        new deleteFileAsyncTask(context, response).execute(path);
    }
}
