package io.palaima.calendar.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class EventEntity implements EventEntityModel {

    private static final DateAdapter DATE_ADAPTER = new DateAdapter();

    public static final Mapper<EventEntity> MAPPER = new Mapper<>(new Factory<>(new Creator<EventEntity>() {
        @Override
        public EventEntity create(long _id, String title, String description, long categoryId, long startDate, long endDate) {
            return new AutoValue_EventEntity(_id, title, description, categoryId, startDate, endDate);
        }
    }));

    public static Marshal marshal() {
        return new Factory<>(new Creator<EventEntity>() {
            @Override
            public EventEntity create(long _id, String title, String description, long categoryId, long startDate, long endDate) {
                return new AutoValue_EventEntity(_id, title, description, categoryId, startDate, endDate);
            }
        }).marshal();
    }
}
