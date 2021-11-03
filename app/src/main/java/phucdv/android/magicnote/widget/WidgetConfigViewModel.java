package phucdv.android.magicnote.widget;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.List;

import phucdv.android.magicnote.data.noteitem.Note;
import phucdv.android.magicnote.ui.BaseViewModel;

public class WidgetConfigViewModel extends BaseViewModel {
    public WidgetConfigViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void initNotes() {
        mNotes = mNoteRepository.getAllNotes();
    }
}
