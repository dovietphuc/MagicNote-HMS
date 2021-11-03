package phucdv.android.magicnote.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import phucdv.android.magicnote.data.BaseItemRepository;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.noteandlabel.NoteWithLabels;
import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.data.noteitem.NoteRepository;

public abstract class BaseViewModel extends AndroidViewModel {

    protected NoteRepository mNoteRepository;
    protected BaseItemRepository mBaseItemRepository;
    protected LiveData<List<Note>> mNotes;
    protected List<NoteWithLabels> mNoteWithLabels;
    protected LifecycleOwner mOwner;
    private int mNumberQueryDoneLabels = 0;
    private boolean mIsQueryDone = false;

    private Callback mCallback;

    public interface Callback{
        public void queryDoneNoteWithLabels(List<NoteWithLabels> noteWithLabels);
    }

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = new NoteRepository(application);
        mBaseItemRepository = new BaseItemRepository(application);
        mNoteWithLabels = new ArrayList<>();
    }

    public LiveData<List<Note>> getListNotes(){
        return mNotes;
    }

    public void init(LifecycleOwner owner){
        mOwner = owner;
        initNotes();
        mNotes.observe(mOwner, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                initNoteWithLabels(notes);
            }
        });
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public abstract void initNotes();

    public void initNoteWithLabels(List<Note> notes){
        for(Note note : notes) {
            NoteWithLabels noteWithLabels = new NoteWithLabels(getApplication(), mOwner, note);
            noteWithLabels.setCallback(new NoteWithLabels.Callback() {
                @Override
                public void doneQueryLabel(List<Label> labels) {
                    onQueryNoteWithLabels();
                }
            });
            mNoteWithLabels.add(noteWithLabels);
        }
    }

    private void onQueryNoteWithLabels(){
        mNumberQueryDoneLabels++;
        mIsQueryDone = mNumberQueryDoneLabels == mNotes.getValue().size();
        if(mIsQueryDone){
            mNumberQueryDoneLabels = 0;
            mIsQueryDone = false;
            if(mCallback != null){
                mCallback.queryDoneNoteWithLabels(mNoteWithLabels);
            }
        }
    }

    public void updateNote(Note note){
        mNoteRepository.updateNote(note);
    }

    public void updateListNote(List<Note> notes){
        mNoteRepository.updateListNote(notes);
    }

    public void deleteNote(long id){
        mNoteRepository.deleteNote(id);
    }

    public void deleteNote(Note note){
        mNoteRepository.deleteNote(note);
    }

    public void deleteListNote(List<Note> notes){
        mNoteRepository.deleteListNote(notes);
    }

    public void moveToArchive(Note note){
        mNoteRepository.moveToArchive(note);
    }

    public void moveToTrash(Note note){
        mNoteRepository.moveToTrash(note);
    }

    public void moveToProcessing(Note note){
        mNoteRepository.moveToProcessing(note);
    }

    public void moveToArchive(List<Note> notes){
        mNoteRepository.moveToArchive(notes);
    }

    public void moveToTrash(List<Note> notes){
        mNoteRepository.moveToTrash(notes);
    }

    public void moveToProcessing(List<Note> notes){
        mNoteRepository.moveToProcessing(notes);
    }

    public void pinOrUnpin(Note note){
        mNoteRepository.pinOrUnpin(note);
    }

    public void pin(Note note){
        mNoteRepository.pin(note);
    }

    public void unpin(Note note){
        mNoteRepository.unpin(note);
    }

    public void updateColor(int color, Note note){
        mNoteRepository.updateColor(color, note);
    }

    public void pin(List<Note> notes){
        mNoteRepository.pin(notes);
    }

    public void unpin(List<Note> notes){
        mNoteRepository.unpin(notes);
    }

    public void updateColor(int color, List<Note> notes){
        mNoteRepository.updateColor(color, notes);
    }
}
