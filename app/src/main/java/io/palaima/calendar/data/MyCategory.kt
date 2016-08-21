package io.palaima.calendar.data

import io.palaima.eventscalendar.data.Category

class MyCategory(private val id: Long, private val name: String) : Category {

    override fun getId(): Long {
        return id
    }

    override fun getName(): String? {
        return name
    }

    override fun toString(): String {
        return "MyCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'
    }

    companion object {

        fun from(categoryEntity: CategoryEntity): MyCategory {
            return MyCategory(categoryEntity._id(), categoryEntity.name())
        }
    }
}
