package io.palaima.calendar.extention

import io.realm.Realm

fun Realm.executeSafe(execute: Realm.(r: Realm) -> Unit) {
    this.executeTransaction {
        execute(it)
    }
    this.close()
}