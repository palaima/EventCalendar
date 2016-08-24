package io.palaima.calendar.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.util.*

@RealmClass
open class Task(
        @PrimaryKey open var id: Long = 0,
        open var title: String = "",
        open var description: String? = null,
        open var startTime: Date = Date(),
        open var endTime: Date = Date()
) : RealmObject() {

}