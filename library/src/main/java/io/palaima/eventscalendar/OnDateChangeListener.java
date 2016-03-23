package io.palaima.eventscalendar;

import android.support.annotation.NonNull;

import java.util.Date;

public abstract class OnDateChangeListener {

    public abstract void onDateChanged(@NonNull Date date);
    public abstract void onCategoryExpanded(long categoryId);
    public abstract void onCategoryCollapsed(long categoryId);

}
