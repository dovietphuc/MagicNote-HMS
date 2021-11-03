package phucdv.android.magicnote.data.noteandlabel;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.noteitem.Note;

public class NoteWithLabels {
    Note mNote;
    LiveData<List<Label>> mLabels;
    NoteLabelDao mNoteLabelDao;
    LifecycleOwner mOwner;
    Callback mCallback;

    public interface Callback{
        public void doneQueryLabel(List<Label> labels);
    }

    public NoteWithLabels(Application application, LifecycleOwner owner, Note note){
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mNoteLabelDao = db.noteLabelDao();
        mNote = note;
        mLabels = mNoteLabelDao.getLabelsByNoteId(mNote.getId());
        mOwner = owner;
        mLabels.observe(mOwner, new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                if(mCallback != null){
                    mCallback.doneQueryLabel(labels);
                }

            }
        });
    }

    public Note getNote(){
        return mNote;
    }

    public LiveData<List<Label>> getLabels(){
        return mLabels;
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }
}
