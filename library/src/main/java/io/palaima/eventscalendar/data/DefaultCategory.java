package io.palaima.eventscalendar.data;

import android.support.annotation.Nullable;

public class DefaultCategory implements Category {

    public static final DefaultCategory INSTANCE = new DefaultCategory();

    private DefaultCategory() {

    }

    @Override
    public long getId() {
        return DEFAULT_ID;
    }

    @Nullable
    public String getName() {
        return null;
    }
}
