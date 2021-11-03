package phucdv.android.magicnote.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
import phucdv.android.magicnote.noteinterface.AsyncResponse;

public class AsyncTaskUtil {
    public static class insertNoteAsyncTask extends AsyncTask<Note, Void, Long> {

        private NoteDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public insertNoteAsyncTask(NoteDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Long doInBackground(final Note... params) {
            return mAsyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(mResponse != null)
                mResponse.processFinish(aLong);
        }
    }

    public static class insertTextItemAsyncTask extends AsyncTask<TextItem, Void, Long> {

        private TextItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public insertTextItemAsyncTask(TextItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Long doInBackground(final TextItem... params) {
            return mAsyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(mResponse != null)
                mResponse.processFinish(aLong);
        }
    }

    public static class insertAllTextItemsAsyncTask extends AsyncTask<List<TextItem>, Void, Long[]> {

        private TextItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public insertAllTextItemsAsyncTask(TextItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Long[] doInBackground(final List<TextItem>... params) {
            return mAsyncTaskDao.insertAll(params[0]);
        }

        @Override
        protected void onPostExecute(Long[] longs) {
            if(mResponse != null)
                mResponse.processFinish(longs);
        }
    }

    public static class insertChecboxItemAsyncTask extends AsyncTask<CheckboxItem, Void, Long> {

        private CheckboxItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;
        public insertChecboxItemAsyncTask(CheckboxItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Long doInBackground(final CheckboxItem... params) {
            return mAsyncTaskDao.insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if(mResponse != null)
                mResponse.processFinish(aLong);
        }
    }

    public static class insertAllCheckboxItemsAsyncTask extends AsyncTask<List<CheckboxItem>, Void, Long[]> {

        private CheckboxItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public insertAllCheckboxItemsAsyncTask(CheckboxItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Long[] doInBackground(final List<CheckboxItem>... params) {
            return mAsyncTaskDao.insertAll(params[0]);
        }

        @Override
        protected void onPostExecute(Long[] longs) {
            if(mResponse != null)
                mResponse.processFinish(longs);
        }
    }

    public static class deleteNoteAsyncTask extends AsyncTask<Long, Void, Void> {

        private NoteDao mNoteDao;
        private TextItemDao mTextItemDao;
        private CheckboxItemDao mCheckboxItemDao;
        private ImageItemDao mImageItemDao;

        public deleteNoteAsyncTask(NoteDao noteDao, TextItemDao textItemDao,
                                   CheckboxItemDao checkboxItemDao, ImageItemDao imageItemDao) {
            mNoteDao = noteDao;
            mTextItemDao = textItemDao;
            mCheckboxItemDao = checkboxItemDao;
            mImageItemDao = imageItemDao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mNoteDao.deleteNoteByID(params[0]);
            mTextItemDao.deleteByParentId(params[0]);
            mCheckboxItemDao.deleteByParentId(params[0]);
            mImageItemDao.deleteByParentId(params[0]);
            return null;
        }
    }

    public static class deleteListNoteAsyncTask extends AsyncTask<List<Note>, Void, Void> {

        private NoteDao mNoteDao;
        private TextItemDao mTextItemDao;
        private CheckboxItemDao mCheckboxItemDao;
        private ImageItemDao mImageItemDao;

        public deleteListNoteAsyncTask(NoteDao noteDao, TextItemDao textItemDao,
                                       CheckboxItemDao checkboxItemDao, ImageItemDao imageItemDao) {
            mNoteDao = noteDao;
            mTextItemDao = textItemDao;
            mCheckboxItemDao = checkboxItemDao;
            mImageItemDao = imageItemDao;
        }

        @Override
        protected Void doInBackground(final List<Note>... params) {
            for(Note note : params[0]){
                mNoteDao.deleteNoteByID(note.getId());
                mTextItemDao.deleteByParentId(note.getId());
                mCheckboxItemDao.deleteByParentId(note.getId());
                mImageItemDao.deleteByParentId(note.getId());
            }
            return null;
        }
    }

    public static class deleteTextItemByParentIdAsyncTask extends AsyncTask<Long, Void, Void> {

        private TextItemDao mAsyncTaskDao;

        public deleteTextItemByParentIdAsyncTask(TextItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.deleteByParentId(params[0]);
            return null;
        }
    }

    public static class deleteTextItemByIdAsyncTask extends AsyncTask<Long, Void, Void> {

        private TextItemDao mAsyncTaskDao;

        public deleteTextItemByIdAsyncTask(TextItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.deleteById(params[0]);
            return null;
        }
    }

    public static class deleteCheckboxItemByParentIdAsyncTask extends AsyncTask<Long, Void, Void> {

        private CheckboxItemDao mAsyncTaskDao;

        public deleteCheckboxItemByParentIdAsyncTask(CheckboxItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.deleteByParentId(params[0]);
            return null;
        }
    }

    public static class deleteCheckboxItemByIdAsyncTask extends AsyncTask<Long, Void, Void> {

        private CheckboxItemDao mAsyncTaskDao;

        public deleteCheckboxItemByIdAsyncTask(CheckboxItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.deleteById(params[0]);
            return null;
        }
    }

    public static class updateNoteAsyncTask extends AsyncTask<Note, Void, Void> {

        private NoteDao mAsyncTaskDao;

        public updateNoteAsyncTask(NoteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    public static class updateNoteAsyncTaskWithResponse extends AsyncTask<Note, Void, Void> {

        private NoteDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public updateNoteAsyncTaskWithResponse(NoteDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Void doInBackground(final Note... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mResponse.processFinish(unused);
        }
    }

    public static class updateListNoteAsyncTask extends AsyncTask<List<Note>, Void, Void> {

        private NoteDao mAsyncTaskDao;

        public updateListNoteAsyncTask(NoteDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<Note>... params) {
            mAsyncTaskDao.updateAll(params[0]);
            return null;
        }
    }

    public static class updateTextItemAsyncTask extends AsyncTask<TextItem, Void, Void> {

        private TextItemDao mAsyncTaskDao;

        public updateTextItemAsyncTask(TextItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final TextItem... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    public static class updateListTextItemAsyncTask extends AsyncTask<List<TextItem>, Void, Void> {

        private TextItemDao mAsyncTaskDao;

        public updateListTextItemAsyncTask(TextItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<TextItem>... params) {
            mAsyncTaskDao.updateAll(params[0]);
            return null;
        }
    }

    public static class updateCheckboxItemAsyncTask extends AsyncTask<CheckboxItem, Void, Void> {

        private CheckboxItemDao mAsyncTaskDao;

        public updateCheckboxItemAsyncTask(CheckboxItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final CheckboxItem... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    public static class updateListCheckboxItemAsyncTask extends AsyncTask<List<CheckboxItem>, Void, Void> {

        private CheckboxItemDao mAsyncTaskDao;

        public updateListCheckboxItemAsyncTask(CheckboxItemDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final List<CheckboxItem>... params) {
            mAsyncTaskDao.updateAll(params[0]);
            return null;
        }
    }

    public static class insertImageItemAsyncTask extends AsyncTask<ImageItem, Void, Void> {

        private ImageItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public insertImageItemAsyncTask(ImageItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Void doInBackground(final ImageItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if(mResponse != null)
                mResponse.processFinish(unused);
            super.onPostExecute(unused);
        }
    }

    public static class deleteImageItemByIdAsyncTask extends AsyncTask<Long, Void, Void> {

        private ImageItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public deleteImageItemByIdAsyncTask(ImageItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.deleteById(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if(mResponse != null)
                mResponse.processFinish(unused);
            super.onPostExecute(unused);
        }
    }

    public static class deleteImageItemByParentIdAsyncTask extends AsyncTask<Long, Void, Void> {

        private ImageItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public deleteImageItemByParentIdAsyncTask(ImageItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            mAsyncTaskDao.deleteByParentId(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if(mResponse != null)
                mResponse.processFinish(unused);
            super.onPostExecute(unused);
        }
    }

    public static class updateImageItemAsyncTask extends AsyncTask<ImageItem, Void, Void> {

        private ImageItemDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public updateImageItemAsyncTask(ImageItemDao dao, AsyncResponse response) {
            mAsyncTaskDao = dao;
            mResponse = response;
        }

        @Override
        protected Void doInBackground(final ImageItem... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if(mResponse != null)
                mResponse.processFinish(unused);
            super.onPostExecute(unused);
        }
    }

    public static class copyAsynTask extends AsyncTask<Void, Void, String>{

        Context mContext;
        Uri srcUri;
        String folder;
        String fileName;
        AsyncResponse mResponse;

        public copyAsynTask(Context mContext, Uri srcUri, String folder, String fileName, AsyncResponse response) {
            this.mContext = mContext;
            this.srcUri = srcUri;
            this.folder = folder;
            this.fileName = fileName;
            mResponse = response;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String path = folder + File.separator + fileName;
            File srcFile = FileHelper.getFileFromUri(mContext, srcUri);
            File targetFile = FileHelper.createFile(folder, fileName);
            try {
                FileHelper.copy(srcFile, targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return path;
        }

        @Override
        protected void onPostExecute(String unused) {
            if(mResponse != null)
                mResponse.processFinish(unused);
            super.onPostExecute(unused);
        }
    }

    public static class insertLabelAsyncTask extends AsyncTask<Label, Void, Long>{

        private LabelDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public insertLabelAsyncTask(LabelDao labelDao, AsyncResponse response){
            mAsyncTaskDao = labelDao;
            mResponse = response;
        }

        @Override
        protected Long doInBackground(Label... labels) {
            return mAsyncTaskDao.insert(labels[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if(mResponse != null) {
                mResponse.processFinish(aLong);
            }
        }
    }

    public static class insertListLabelAsyncTask extends AsyncTask<List<Label>, Void, Long[]>{

        private LabelDao mAsyncTaskDao;
        private AsyncResponse mResponse;

        public insertListLabelAsyncTask(LabelDao labelDao, AsyncResponse response){
            mAsyncTaskDao = labelDao;
            mResponse = response;
        }

        @Override
        protected Long[] doInBackground(List<Label>... lists) {
            return mAsyncTaskDao.insertAll(lists[0]);
        }

        @Override
        protected void onPostExecute(Long[] longs) {
            super.onPostExecute(longs);
            if(mResponse != null){
                mResponse.processFinish(longs);
            }
        }
    }

    public static class deleteLabelAsyncTask extends AsyncTask<Label, Void, Void>{

        private LabelDao mAsyncTaskDao;

        public deleteLabelAsyncTask(LabelDao labelDao){
            mAsyncTaskDao = labelDao;
        }

        @Override
        protected Void doInBackground(Label... labels) {
            mAsyncTaskDao.delete(labels[0].getId());
            return null;
        }
    }

    public static class insertLabelNoteAsyncTask extends AsyncTask<NoteLabel, Void, Long>{

        AsyncResponse mResponse;
        NoteLabelDao mNoteLabelDao;

        public insertLabelNoteAsyncTask(NoteLabelDao noteLabelDao, AsyncResponse response){
            mNoteLabelDao = noteLabelDao;
            mResponse = response;
        }

        @Override
        protected Long doInBackground(NoteLabel... noteLabels) {
            Long id = mNoteLabelDao.insert(noteLabels[0]);
            return id;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if(mResponse != null) {
                mResponse.processFinish(aLong);
            }
        }
    }

    public static class insertListLabelNoteAsyncTask extends AsyncTask<List<NoteLabel>, Void, Long[]>{

        AsyncResponse mResponse;
        NoteLabelDao mNoteLabelDao;

        public insertListLabelNoteAsyncTask(NoteLabelDao noteLabelDao, AsyncResponse response){
            mNoteLabelDao = noteLabelDao;
            mResponse = response;
        }

        @Override
        protected Long[] doInBackground(List<NoteLabel>... noteLabels) {
            return mNoteLabelDao.insertAll(noteLabels[0]);
        }

        @Override
        protected void onPostExecute(Long[] aLong) {
            super.onPostExecute(aLong);
            if(mResponse != null) {
                mResponse.processFinish(aLong);
            }
        }
    }

    public static class deleteLabelNoteAsyncTask extends AsyncTask<NoteLabel, Void, Void>{

        NoteLabelDao mNoteLabelDao;

        public deleteLabelNoteAsyncTask(NoteLabelDao noteLabelDao){
            mNoteLabelDao = noteLabelDao;
        }

        @Override
        protected Void doInBackground(NoteLabel... noteLabels) {
            mNoteLabelDao.delete(noteLabels[0].getLabel_id());
            return null;
        }
    }

    public static class deleteLabelNoteForNoteAsyncTask extends AsyncTask<Long, Void, Void>{

        NoteLabelDao mNoteLabelDao;

        public deleteLabelNoteForNoteAsyncTask(NoteLabelDao noteLabelDao){
            mNoteLabelDao = noteLabelDao;
        }

        @Override
        protected Void doInBackground(Long... note_id) {
            mNoteLabelDao.deleteForNote(note_id[0]);
            return null;
        }
    }

    public static class insertLabelForNoteAsynTask extends AsyncTask<Void, Void, Void>{
        LifecycleOwner mOwner;
        LabelDao mLabelDao;
        NoteLabelDao mNoteLabelDao;
        long mNoteId;
        Label mLabel;
        Label mConflict;

        public insertLabelForNoteAsynTask(LifecycleOwner owner, LabelDao labelDao, NoteLabelDao noteLabelDao, long note_id, Label label){
            mLabelDao = labelDao;
            mNoteLabelDao = noteLabelDao;
            mNoteId = note_id;
            mLabel = label;
            mOwner = owner;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mNoteLabelDao.deleteForNote(mNoteId);
            Long label_id = mLabelDao.insert(mLabel);
            if(label_id > 0){
                final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                mNoteLabelDao.insert(new NoteLabel(mNoteId, label_id, firebaseUser != null ? firebaseUser.getUid() : null, true));
            } else {
                mConflict = mLabel;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if(mConflict != null){
                mLabelDao.getLabelByName(mConflict.getName()).observe(mOwner, new Observer<Label>() {
                    @Override
                    public void onChanged(Label label) {
                        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        new insertLabelNoteAsyncTask(mNoteLabelDao, null).execute(new NoteLabel(mNoteId, label.getId(), firebaseUser != null ? firebaseUser.getUid() : null, true));
                    }
                });
            }
        }
    }

    public static class insertListLabelForNoteAsynTask extends AsyncTask<Void, Void, Void>{

        LifecycleOwner mOwner;
        LabelDao mLabelDao;
        NoteLabelDao mNoteLabelDao;
        long mNoteId;
        List<Label> mLabels;
        List<Label> mConflicts;

        public insertListLabelForNoteAsynTask(LifecycleOwner owner, LabelDao labelDao, NoteLabelDao noteLabelDao, long note_id, List<Label> labels){
            mLabelDao = labelDao;
            mNoteLabelDao = noteLabelDao;
            mNoteId = note_id;
            mLabels = labels;
            mOwner = owner;
            mConflicts = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mNoteLabelDao.deleteForNote(mNoteId);
            for (Label label : mLabels) {
                Long label_id = mLabelDao.insert(label);
                if (label_id > 0) {
                    final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    mNoteLabelDao.insert(new NoteLabel(mNoteId, label_id, firebaseUser != null ? firebaseUser.getUid() : null, true));
                } else {
                    mConflicts.add(label);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            for(int i = 0; i < mConflicts.size(); i++){
                Label label = mConflicts.get(i);
                final int index = i;
                mLabelDao.getLabelByName(label.getName()).observe(mOwner, new Observer<Label>() {
                    @Override
                    public void onChanged(Label label) {
                        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        new insertLabelNoteAsyncTask(mNoteLabelDao, new AsyncResponse() {
                            @Override
                            public void processFinish(Object output) {
                                if(index == mConflicts.size() - 1){
                                    new deleteLabelIfNeedAsyncTask(mNoteLabelDao).execute();
                                }
                            }
                        }).execute(new NoteLabel(mNoteId, label.getId(), firebaseUser != null ? firebaseUser.getUid() : null, true));
                    }
                });
            }
        }
    }

    public static class deleteLabelIfNeedAsyncTask extends AsyncTask<Void, Void, Void>{

        NoteLabelDao mNoteLabelDao;

        public deleteLabelIfNeedAsyncTask(NoteLabelDao noteLabelDao){
            mNoteLabelDao = noteLabelDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mNoteLabelDao.deleteLabelIfNeed();
            return null;
        }
    }
}
