package phucdv.android.magicnote.data;

import androidx.room.TypeConverter;

import java.util.Calendar;

public class Converters {
    @TypeConverter
    public static Long calendarToDatestamp(Calendar calendar){
        return calendar.getTimeInMillis();
    }

    @TypeConverter
    public static Calendar datestampToCalendar(long value){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);
        return calendar;
    }
}
