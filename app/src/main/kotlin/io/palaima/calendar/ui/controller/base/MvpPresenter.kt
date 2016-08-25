package io.palaima.calendar.ui.controller.base

interface MvpPresenter<V: MvpView> {

    fun bind(view: V)

    fun unbind(view: V)

    fun view(): V

    fun hasView(): Boolean

    fun destroy()
}
