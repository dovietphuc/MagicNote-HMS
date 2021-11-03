package phucdv.android.magicnote.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.data.label.Label;
import phucdv.android.magicnote.ui.label.LabelSelectorDialog;

public class LabelSelectorAdapter extends RecyclerView.Adapter<LabelSelectorAdapter.ViewHolder> {

    private List<Label> mLabels;
    private List<Label> mLabelSelected;

    public interface OnLabelSelectedChangeListener{
        public void onChange(List<Label> selectedLabel, Label label, boolean selected);
    }

    private OnLabelSelectedChangeListener mOnLabelSelectedChangeListener;

    public LabelSelectorAdapter(){
        mLabels = new ArrayList<>();
        mLabelSelected = new ArrayList<>();
    }

    public void setValues(List<Label> labels){
        mLabels = labels;
        notifyDataSetChanged();
    }

    public void setOnLabelSelectedChangeListener(OnLabelSelectedChangeListener listener){
        mOnLabelSelectedChangeListener = listener;
    }

    public void deselect(){
        mLabelSelected.clear();
        notifyDataSetChanged();
    }

    public List<Label> getLabelSelected() {
        return mLabelSelected;
    }

    public void setLabelSelected(List<Label> labelSelected) {
        this.mLabelSelected = new ArrayList<>(labelSelected);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mLabels.get(position));
    }

    @Override
    public int getItemCount() {
        return mLabels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public Label mLabel;
        public View mView;
        public CheckBox mCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            mCheckBox = itemView.findViewById(R.id.checkbox_label);
            mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(mLabel != null) {
                    if(isChecked) {
                        mLabelSelected.add(mLabel);
                    } else {
                        mLabelSelected.removeIf(label -> label.getName().equals(mLabel.getName()));
                    }
                }
                if(mOnLabelSelectedChangeListener != null){
                    mOnLabelSelectedChangeListener.onChange(mLabelSelected, mLabel, isChecked);
                }
            });
        }

        public void bind(Label label){
            mLabel = label;
            mCheckBox.setText(label.getName());
            mCheckBox.setChecked(contains(label));
        }

        public boolean contains(Label label){
            for(Label lb : mLabelSelected){
                if(lb.getName().equals(label.getName())){
                    return true;
                }
            }
            return false;
        }
    }
}
