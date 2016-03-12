package io.palaima.eventscalendar.data;

import android.support.annotation.Nullable;

public interface Category {

    int DEFAULT_ID = -1;

    long getId();

    @Nullable
    String getName();
}
