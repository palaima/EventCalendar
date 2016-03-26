package io.palaima.eventscalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class DefaultTimeConverter {

    public enum Type {
        AM_PM, HOUR_24
    }

    private final SimpleDateFormat formatter24h;
    private final SimpleDateFormat formatter12h;
    private final Calendar calendar = Calendar.getInstance();
    private final Map<Integer, String> cache = new HashMap<>();

    public DefaultTimeConverter() {
        formatter24h = new SimpleDateFormat("HH:mm", Locale.getDefault());
        formatter12h = new SimpleDateFormat("hh a", Locale.getDefault());
    }

    public String interpretTime(int hour, Type type) {
        if (!cache.containsKey(hour)) {
            try {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 0);
                cache.put(hour, (type == Type.HOUR_24 ? formatter24h : formatter12h).format(calendar.getTime()));
            } catch (Exception e) {
                Timber.e(e, "error while formatting time");
                return "";
            }
        }

        return cache.get(hour);
    }
}
