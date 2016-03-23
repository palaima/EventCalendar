package io.palaima.calendar.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CategoryEntity implements CategoryEntityModel {

    public static final Mapper<CategoryEntity> MAPPER = new Mapper<>(new Mapper.Creator<CategoryEntity>() {
        @Override public CategoryEntity create(long _id, String name) {
            return new AutoValue_CategoryEntity(_id, name);
        }
    });

    public static final class Marshal extends CategoryEntityMarshal<Marshal> { }
}
