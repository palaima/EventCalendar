package io.palaima.calendar.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CategoryEntity implements CategoryEntityModel {

    public static final Mapper<CategoryEntity> MAPPER = new Mapper<>(new Factory<>(new Creator<CategoryEntity>() {
        @Override public CategoryEntity create(long _id, String name) {
            return new AutoValue_CategoryEntity(_id, name);
        }
    }));

    public static Marshal marshal() {
        return new Factory<>(new Creator<CategoryEntity>() {
            @Override public CategoryEntity create(long _id, String name) {
                return new AutoValue_CategoryEntity(_id, name);
            }
        }).marshal();
    }
}
