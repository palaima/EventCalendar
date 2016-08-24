package io.palaima.calendar.data

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Type(
        @PrimaryKey open var id: Long = 0,
        open var name: String = "",
        open var tasks: RealmList<Task> = RealmList()
) : RealmObject() {

}
