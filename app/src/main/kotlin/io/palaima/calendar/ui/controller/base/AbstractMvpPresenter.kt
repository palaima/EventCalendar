package io.palaima.calendar.ui.controller.base

abstract class AbstractMvpPresenter<V: MvpView>: MvpPresenter<V> {

    private var view: V? = null

    override fun bind(view: V) {
        this.view = view
    }

    override fun unbind(view: V) {
        this.view = null
    }

    override fun hasView(): Boolean {
        return view != null
    }

    override fun view(): V {
        return view!!
    }

    override fun destroy() {
        // Hook to clean resources
    }
}
