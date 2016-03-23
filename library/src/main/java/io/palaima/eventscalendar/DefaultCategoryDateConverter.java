package io.palaima.eventscalendar;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DefaultCategoryDateConverter {

    public enum Type {
        SHORT, LONG
    }

    private final SimpleDateFormat shortFormatter;
    private final SimpleDateFormat normalFormatter;

    public DefaultCategoryDateConverter() {
        shortFormatter = new SimpleDateFormat("EEE M/dd", Locale.getDefault());
        normalFormatter = new SimpleDateFormat("EEEEE M/dd", Locale.getDefault());
    }

    public String getValue(@NonNull Date date, @NonNull Type type) {
       return type == Type.SHORT ? shortFormatter.format(date.getTime()).toUpperCase() : normalFormatter.format(date.getTime()).toUpperCase();
    }
}
