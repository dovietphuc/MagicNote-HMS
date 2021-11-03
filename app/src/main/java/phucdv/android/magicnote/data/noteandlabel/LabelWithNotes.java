package phucdv.android.magicnote.data.noteandlabel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.noteitem.Note;

public class LabelWithNotes {

    Label mLabel;
    LiveData<List<Note>> mNotes;
    NoteLabelDao mNoteLabelDao;

    public LabelWithNotes(Application application, Label label){
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mNoteLabelDao = db.noteLabelDao();
        mLabel = label;
        mNotes = mNoteLabelDao.getNotesByLabelId(mLabel.getId());
    }

    public Label getLabel(){
        return mLabel;
    }

    public LiveData<List<Note>> getNotes(){
        return mNotes;
    }
}
