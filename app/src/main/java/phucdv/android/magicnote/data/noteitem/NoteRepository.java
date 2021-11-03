package phucdv.android.magicnote.data.noteitem;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import phucdv.android.magicnote.data.NoteRoomDatabase;
import phucdv.android.magicnote.data.checkboxitem.CheckboxItemDao;
import phucdv.android.magicnote.data.imageitem.ImageItemDao;
import phucdv.android.magicnote.data.textitem.TextItemDao;
import phucdv.android.magicnote.noteinterface.AsyncResponse;
import phucdv.android.magicnote.util.AsyncTaskUtil;

public class NoteRepository {
    private NoteDao mNoteDao;
    private TextItemDao mTextItemDao;
    private CheckboxItemDao mCheckboxItemDao;
    private ImageItemDao mImageItemDao;
    private LiveData<List<Note>> mAllNotes;
    private LiveData<List<Note>> mProcessingNotes;
    private LiveData<List<Note>> mArchiveNotes;
    private LiveData<List<Note>> mTrashNotes;
    private MutableLiveData<Long> mLastNodeInsertedID;
    private LiveData<Note> mNote;

    public NoteRepository(Application application){
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        mNoteDao = db.noteDao();
        mTextItemDao = db.textItemDao();
        mCheckboxItemDao = db.checkboxItemDao();
        mImageItemDao = db.imageItemDao();
        mAllNotes = mNoteDao.getNotes();
        mProcessingNotes = mNoteDao.getNotesInProcessing();
        mArchiveNotes = mNoteDao.getNotesInArchive();
        mTrashNotes = mNoteDao.getNotesInTrash();
        mLastNodeInsertedID = new MutableLiveData<>();
    }

    public void initNote(long id){
        mNote = mNoteDao.getNotesById(id);
    }

    public LiveData<Note> getNote(){
        return mNote;
    }

    public LiveData<Long> getLastNodeInsertedID(){
        return mLastNodeInsertedID;
    }

    public LiveData<List<Note>> getAllNotes(){
        return mAllNotes;
    }

    public LiveData<List<Note>> getNotesInProcessing(){
        return mProcessingNotes;
    }

    public LiveData<List<Note>> getNotesInArchive(){
        return mArchiveNotes;
    }

    public LiveData<List<Note>> getNotesInTrash(){
        return mTrashNotes;
    }

    public void insert(Note note){
        new AsyncTaskUtil.insertNoteAsyncTask(mNoteDao, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                mLastNodeInsertedID.setValue((Long) output);
            }
        }).execute(note);
    }

    public void insert(Note note, AsyncResponse response){
        new AsyncTaskUtil.insertNoteAsyncTask(mNoteDao, response).execute(note);
    }

    public void deleteNote(long id){
        new AsyncTaskUtil.deleteNoteAsyncTask(mNoteDao, mTextItemDao, mCheckboxItemDao, mImageItemDao).execute(id);
    }

    public void deleteNote(Note note){
        new AsyncTaskUtil.deleteNoteAsyncTask(mNoteDao, mTextItemDao, mCheckboxItemDao, mImageItemDao).execute(note.getId());
    }

    public void deleteListNote(List<Note> notes){
        new AsyncTaskUtil.deleteListNoteAsyncTask(mNoteDao, mTextItemDao, mCheckboxItemDao, mImageItemDao).execute(notes);
    }

    public void updateNote(Note note){
        new AsyncTaskUtil.updateNoteAsyncTask(mNoteDao).execute(note);
    }

    public void updateNote(Note note, AsyncResponse response){
        new AsyncTaskUtil.updateNoteAsyncTaskWithResponse(mNoteDao, response).execute(note);
    }

    public void updateListNote(List<Note> notes){
        new AsyncTaskUtil.updateListNoteAsyncTask(mNoteDao).execute(notes);
    }

    public void moveToArchive(Note note){
        note.setIs_archive(true);
        note.setIs_deleted(false);
        updateNote(note);
    }

    public void moveToTrash(Note note){
        note.setIs_archive(false);
        note.setIs_deleted(true);
        updateNote(note);
    }

    public void moveToProcessing(Note note){
        note.setIs_archive(false);
        note.setIs_deleted(false);
        updateNote(note);
    }

    public void moveToArchive(List<Note> notes){
        for(Note note : notes) {
            note.setIs_archive(true);
            note.setIs_deleted(false);
        }
        updateListNote(notes);
    }

    public void moveToTrash(List<Note> notes){
        for(Note note : notes) {
            note.setIs_archive(false);
            note.setIs_deleted(true);
        }
        updateListNote(notes);
    }

    public void moveToProcessing(List<Note> notes){
        for(Note note : notes) {
            note.setIs_archive(false);
            note.setIs_deleted(false);
        }
        updateListNote(notes);
    }

    public void pinOrUnpin(Note note){
        note.setIs_pinned(!note.isIs_pinned());
        updateNote(note);
    }

    public void pin(Note note){
        note.setIs_pinned(true);
        updateNote(note);
    }

    public void unpin(Note note){
        note.setIs_pinned(false);
        updateNote(note);
    }

    public void updateColor(int color, Note note){
        note.setColor(color);
        updateNote(note);
    }

    public void pin(List<Note> notes){
        for(Note note : notes){
            note.setIs_pinned(true);
        }
        updateListNote(notes);
    }

    public void unpin(List<Note> notes){
        for(Note note : notes){
            note.setIs_pinned(false);
        }
        updateListNote(notes);
    }

    public void updateColor(int color, List<Note> notes){
        for(Note note : notes){
            note.setColor(color);
        }
        updateListNote(notes);
    }
}