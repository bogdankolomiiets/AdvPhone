package com.epam.rd.advphone.adapters;

import androidx.databinding.BindingConversion;

import com.epam.rd.advphone.R;
import com.epam.rd.advphone.util.AppContext;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LongDateTimeAdapter {
    private static final int MILLISECONDS_IN_ONE_HOUR = 3600000;
    private static final int MILLISECONDS_IN_ONE_MINUTE = 60000;
    private static final int MILLISECONDS_IN_ONE_SECOND = 1000;
    private static final int MINUTES_IN_ONE_HOUR = 60;
    private static SimpleDateFormat simpleDateFormat;

    static {
        String localeLanguage = Locale.getDefault().getLanguage();
        if (localeLanguage.equals("en")) {
            simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        } else {
            simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        }
    }

    @BindingConversion
    public static String longTimeToString(Long value) {
        if (value != null) {
            long currentTime = System.currentTimeMillis();
            long msDiff = currentTime - value;

            if (msDiff <= MILLISECONDS_IN_ONE_MINUTE) {
                return msDiff / MILLISECONDS_IN_ONE_SECOND + " " + AppContext.getContext().getString(R.string.seconds);
            } else if (msDiff <= MILLISECONDS_IN_ONE_HOUR) {
                return (msDiff / MILLISECONDS_IN_ONE_SECOND) / MINUTES_IN_ONE_HOUR + " " + AppContext.getContext().getString(R.string.minutes);
            } else {
                return simpleDateFormat.format(value);
            }
        }
        return "";
    }
}
