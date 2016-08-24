package io.palaima.calendar.data

import io.palaima.eventscalendar.data.Category

class CalendarCategory(private val id: Long, private val name: String) : Category {

    override fun getId(): Long {
        return id
    }

    override fun getName(): String {
        return name
    }

    override fun toString(): String {
        return "CalendarCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'
    }

    companion object {

        fun from(type: Type): CalendarCategory {
            return CalendarCategory(type.id, type.name)
        }
    }
}
