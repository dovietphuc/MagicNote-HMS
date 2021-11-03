package phucdv.android.magicnote.util;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import phucdv.android.magicnote.R;

public class CheckableImageButton extends androidx.appcompat.widget.AppCompatImageButton {

    private boolean mIsChecked;
    private int mBackGroundUnCheckedColor;
    private int mBackGroundCheckedColor;
    private OnCheckableButtonListener mOnCheckableButtonListener = new OnCheckableButtonListener() {
        @Override
        public void onCheckChange(View view, boolean isCheck) {

        }
    };

    public CheckableImageButton(@NonNull Context context) {
        super(context);
        mBackGroundUnCheckedColor = getResources().getColor(R.color.unchecked_button);
        mBackGroundCheckedColor = getResources().getColor(R.color.checked_button);
        setChecked(false);
    }

    public CheckableImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mBackGroundUnCheckedColor = getResources().getColor(R.color.unchecked_button);
        mBackGroundCheckedColor = getResources().getColor(R.color.checked_button);
        setChecked(false);
    }

    public CheckableImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBackGroundUnCheckedColor = getResources().getColor(R.color.unchecked_button);
        mBackGroundCheckedColor = getResources().getColor(R.color.checked_button);
        setChecked(false);
    }

    public void setChecked(boolean checked){
        mIsChecked = checked;
        setBackgroundColor(mIsChecked ? mBackGroundCheckedColor : mBackGroundUnCheckedColor);
        mOnCheckableButtonListener.onCheckChange(this, mIsChecked);
    }

    public boolean isChecked(){
        return mIsChecked;
    }

    public int getBackGroundUnCheckedColor() {
        return mBackGroundUnCheckedColor;
    }

    public void setBackGroundUnCheckedColor(int mBackGroundUnCheckedColor) {
        this.mBackGroundUnCheckedColor = mBackGroundUnCheckedColor;
    }

    public int getBackGroundCheckedColor() {
        return mBackGroundCheckedColor;
    }

    public void setBackGroundCheckedColor(int mBackGroundCheckedColor) {
        this.mBackGroundCheckedColor = mBackGroundCheckedColor;
    }

    public void setOnCheckableButtonListener(OnCheckableButtonListener checkableButtonListener){
        mOnCheckableButtonListener = checkableButtonListener;
    }

    public OnCheckableButtonListener getOnCheckableButtonListener(){
        return mOnCheckableButtonListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP){
            mIsChecked = !mIsChecked;
            setBackgroundColor(mIsChecked ? mBackGroundCheckedColor : mBackGroundUnCheckedColor);
            mOnCheckableButtonListener.onCheckChange(this, mIsChecked);
        }
        return super.onTouchEvent(event);
    }

    public interface OnCheckableButtonListener{
        public void onCheckChange(View view, boolean isCheck);
    }
}
