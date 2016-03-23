package io.palaima.calendar.data;

import android.support.annotation.Nullable;

import io.palaima.eventscalendar.data.Category;

public final class MyCategory implements Category {

    private final long id;
    private final String name;

    public MyCategory(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override public long getId() {
        return id;
    }

    @Nullable @Override public String getName() {
        return name;
    }

    public static MyCategory from(CategoryEntity categoryEntity) {
        return new MyCategory(categoryEntity._id(), categoryEntity.name());
    }

    @Override public String toString() {
        return "MyCategory{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
