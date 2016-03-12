package io.palaima.eventscalendar;

import android.support.annotation.NonNull;

import java.util.List;

import io.palaima.eventscalendar.data.Category;

public class Transaction {

    @NonNull
    private final CalendarView calendarView;

    @NonNull
    private final Config.Builder configBuilder;

    protected Transaction(@NonNull CalendarView calendarView, @NonNull Config config) {
        this.calendarView = calendarView;
        this.configBuilder = new Config.Builder(calendarView, config);
    }

    public Transaction type(@NonNull CalendarView.Mode mode) {
        configBuilder.mode(mode);
        return this;
    }

    public Transaction categories(List<? extends Category> categories) {
        configBuilder.categories(categories);
        return this;
    }

    public CalendarView start() {
        calendarView.setConfig(configBuilder.build());
        return calendarView;
    }
}
