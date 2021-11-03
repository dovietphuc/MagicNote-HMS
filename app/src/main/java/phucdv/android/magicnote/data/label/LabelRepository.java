package phucdv.android.magicnote.data.label;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.List;

import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.data.noteandlabel.NoteLabelDao;
import phucdv.android.magicnote.util.AsyncTaskUtil;

public class LabelRepository {
    private LabelDao mLabelDao;
    private NoteLabelDao mNoteLabelDao;

    public LabelRepository(Application application){
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mLabelDao = db.labelDao();
        mNoteLabelDao = db.noteLabelDao();
    }

    public LiveData<List<Label>> getAllLabels(){
        return mLabelDao.getAllLabels();
    }

    public LiveData<Label> getLabelById(long id){
        return mLabelDao.getLabelById(id);
    }

    public LiveData<Label> getLabelByName(String name){
        return mLabelDao.getLabelByName(name);
    }

    public void insert(Label label){
        new AsyncTaskUtil.insertLabelAsyncTask(mLabelDao, null).execute(label);
    }

    public void insertAll(List<Label> labels){
        new AsyncTaskUtil.insertListLabelAsyncTask(mLabelDao, null).execute(labels);
    }

    public void insertForNote(LifecycleOwner owner, long note_id, Label label){
        new AsyncTaskUtil.insertLabelForNoteAsynTask(owner, mLabelDao, mNoteLabelDao, note_id, label).execute();
    }

    public void insertAllForNote(LifecycleOwner owner, long note_id, List<Label> labels){
        new AsyncTaskUtil.insertListLabelForNoteAsynTask(owner, mLabelDao, mNoteLabelDao, note_id, labels).execute();
    }

    public void delete(Label label){
        new AsyncTaskUtil.deleteLabelAsyncTask(mLabelDao).execute(label);
    }
}
