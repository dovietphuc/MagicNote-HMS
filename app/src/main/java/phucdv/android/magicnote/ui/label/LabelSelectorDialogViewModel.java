package phucdv.android.magicnote.ui.label;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.data.label.LabelRepository;

public class LabelSelectorDialogViewModel extends AndroidViewModel {
    private LabelRepository mLabelRepository;
    private LiveData<List<Label>> mLabels;

    public LabelSelectorDialogViewModel(Application application){
        super(application);
        mLabelRepository = new LabelRepository(application);
        mLabels = mLabelRepository.getAllLabels();
    }

    public LiveData<List<Label>> getLabels(){
        return mLabels;
    }

}
