package phucdv.android.magicnote.ui.label;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.adapter.LabelSelectorAdapter;
import phucdv.android.magicnote.data.label.Label;

public class LabelSelectorDialog extends DialogFragment {
    private LabelSelectorAdapter mAdapter;
    private Callback mCallback;

    public interface Callback{
        public void onDeselect();
    }

    public LabelSelectorDialog(){
        super();
        mAdapter = new LabelSelectorAdapter();
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    public void setSelectedLabels(List<Label> labels){
        mAdapter.setLabelSelected(labels);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_label, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list_label);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        LabelSelectorDialogViewModel mViewModel = new ViewModelProvider(this).get(LabelSelectorDialogViewModel.class);

        mViewModel.getLabels().observe(getViewLifecycleOwner(), mAdapter::setValues);

        Button deselectButton = view.findViewById(R.id.btnDeselect);
        deselectButton.setOnClickListener(v -> {
            mAdapter.deselect();
            mCallback.onDeselect();
        });

        Button doneButton = view.findViewById(R.id.btnDone);
        doneButton.setOnClickListener(v -> {
            dismiss();
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void showDialog(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        show(fm, "color_dialog");
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void setOnLabelSelectedChangeListener(LabelSelectorAdapter.OnLabelSelectedChangeListener listener){
        mAdapter.setOnLabelSelectedChangeListener(listener);
    }
}
