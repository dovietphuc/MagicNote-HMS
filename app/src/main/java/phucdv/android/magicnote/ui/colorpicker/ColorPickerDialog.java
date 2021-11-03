package phucdv.android.magicnote.ui.colorpicker;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import phucdv.android.magicnote.R;
import phucdv.android.magicnote.adapter.ColorPickerAdapter;

public class ColorPickerDialog extends DialogFragment {

    public static final int COLOR_NONE = -1;
    private RecyclerView mRecyclerView;
    private ColorPickerAdapter mPickerAdapter;
    private ColorPickerAdapter.OnColorPickerListener mOnColorPickerListener;
    private int mExitsColor = COLOR_NONE;
    private boolean mShouldShowNone = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.color_picker_layout, container, false);
        if(view instanceof RecyclerView){
            mRecyclerView = (RecyclerView) view;
            int[] colors = getResources().getIntArray(
                    mShouldShowNone ? R.array.background_color_with_none : R.array.background_color);
            colors[colors.length - 1] = mShouldShowNone ? COLOR_NONE : colors[colors.length - 1];
            mPickerAdapter = new ColorPickerAdapter(colors);
            mPickerAdapter.setExitsColor(mExitsColor);
            mPickerAdapter.setOnColorPickerListener(mOnColorPickerListener);
            mRecyclerView.setAdapter(mPickerAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        }
        return view;
    }

    public void setExitsColor(int color){
        mExitsColor = color;
    }

    public void setOnColorPickerListener(ColorPickerAdapter.OnColorPickerListener onColorPickerListener){
        mOnColorPickerListener = onColorPickerListener;
    }

    public boolean isShouldShowNone() {
        return mShouldShowNone;
    }

    public void setShouldShowNone(boolean shouldShowNone) {
        this.mShouldShowNone = shouldShowNone;
    }

    public void showDialog(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        show(fm, "color_dialog");
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        mOnColorPickerListener.onDismiss(dialog);
        super.onDismiss(dialog);
    }
}
