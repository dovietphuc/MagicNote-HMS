package phucdv.android.magicnote.ui.processing;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.ui.BaseViewModel;

public class ProcessingViewModel extends BaseViewModel {

    public ProcessingViewModel(Application application) {
        super(application);
    }

    @Override
    public void initNotes() {
        mNotes = mNoteRepository.getNotesInProcessing();
    }
}