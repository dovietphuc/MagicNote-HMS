package phucdv.android.magicnote.ui.datetimepicker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import phucdv.android.magicnote.R;

public class DateTimePickerDialog extends DialogFragment implements View.OnClickListener {

    public interface DateTimePickerDialogListener{
        public void onDatePicked(DatePicker v, int day, int month, int year);
        public void onTimePicked(TimePicker v, int hour, int min);
        public void onConfirm(DateTimePickerDialog dialog, int day, int month, int year, int hour, int min);
        public void onCancel(DateTimePickerDialog dialog, int day, int month, int year, int hour, int min);
    }

    private DateTimePickerDialogListener mDateTimePickerDialogListener;

    private Context mContext;
    private Button mPickDate;
    private Button mPickTime;
    private Button mCancel;
    private Button mConfirm;
    private Calendar mCalendar;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    public int getYear() {
        return mYear;
    }

    public void setYear(int mYear) {
        this.mYear = mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int mDay) {
        this.mDay = mDay;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int mHour) {
        this.mHour = mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int mMinute) {
        this.mMinute = mMinute;
    }

    public DateTimePickerDialog(Context context){
        mContext = context;
    }

    public void setDateTimePickerDialogListener(DateTimePickerDialogListener dateTimePickerDialogListener){
        mDateTimePickerDialogListener = dateTimePickerDialogListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_time_picker_dialog, container, false);
        mPickDate = view.findViewById(R.id.btnPickDate);
        mPickTime = view.findViewById(R.id.btnPickTime);
        mCancel = view.findViewById(R.id.btnCancel);
        mConfirm = view.findViewById(R.id.btnConfirm);

        mPickDate.setOnClickListener(this);
        mPickTime.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        mConfirm.setOnClickListener(this);

        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);

        mPickDate.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        mPickTime.setText(mHour + ":" + mMinute);
        return view;
    }

    public void showDialog(AppCompatActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        show(fm, "datetime_dialog");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPickDate:
                showDatePickerDialog();
                break;
            case R.id.btnPickTime:
                showTimePickerDialog();
                break;
            case R.id.btnCancel:
                if (mDateTimePickerDialogListener != null){
                    mDateTimePickerDialogListener.onCancel(this, mDay, mMonth, mYear, mHour, mMinute);
                } else {
                    dismiss();
                }
                break;
            case R.id.btnConfirm:
                if (mDateTimePickerDialogListener != null){
                    mDateTimePickerDialogListener.onConfirm(this, mDay, mMonth, mYear, mHour, mMinute);
                } else {
                    dismiss();
                }
                break;
        }
    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker v, int y, int m, int d) {
                        mPickDate.setText(d + "/" + (m + 1) + "/" + y);
                        mYear = y;
                        mMonth = m;
                        mDay = d;
                        if(mDateTimePickerDialogListener != null){
                            mDateTimePickerDialogListener.onDatePicked(v, d, m, y);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour,
                                          int minute) {
                        mPickTime.setText(hour + ":" + minute);
                        mHour = hour;
                        mMinute = minute;
                        if (mDateTimePickerDialogListener != null){
                            mDateTimePickerDialogListener.onTimePicked(view, hour, minute);
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}