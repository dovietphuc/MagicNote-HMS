package phucdv.android.magicnote.data.noteandlabel;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.noteinterface.AsyncResponse;
import phucdv.android.magicnote.util.AsyncTaskUtil;

public class NoteLabelRepository {
    private NoteLabelDao mNoteLabelDao;

    public NoteLabelRepository(Application application){
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mNoteLabelDao = db.noteLabelDao();
    }

    public LiveData<List<Note>> getNotesByLabelId(long labelId){
        return null;
    }

    public void insert(NoteLabel noteLabel, AsyncResponse response){
        new AsyncTaskUtil.insertLabelNoteAsyncTask(mNoteLabelDao, response).execute(noteLabel);
    }

    public void insertAll(List<NoteLabel> noteLabels, AsyncResponse response){
        new AsyncTaskUtil.insertListLabelNoteAsyncTask(mNoteLabelDao, response).execute(noteLabels);
    }

    public void delete(NoteLabel noteLabel){
        new AsyncTaskUtil.deleteLabelNoteAsyncTask(mNoteLabelDao).execute(noteLabel);
    }

    public void deleteForNote(long note_id){
        new AsyncTaskUtil.deleteLabelNoteForNoteAsyncTask(mNoteLabelDao).execute(note_id);
    }


    public void deleteLabelIfNeed(){
        new AsyncTaskUtil.deleteLabelIfNeedAsyncTask(mNoteLabelDao).execute();
    }
}
