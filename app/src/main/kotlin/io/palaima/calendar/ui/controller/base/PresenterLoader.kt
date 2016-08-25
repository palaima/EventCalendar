package io.palaima.calendar.ui.controller.base

import android.content.Context
import android.content.Loader

class PresenterLoader<P: MvpPresenter<*>>(context: Context, private val create: () -> P): Loader<P>(context) {

    private var presenter: P? = null

    override fun onStartLoading() {
        super.onStartLoading()

        if (presenter == null)
            forceLoad()
        else
            deliverResult(presenter)
    }

    override fun onForceLoad() {
        super.onForceLoad()
        presenter = create()

        deliverResult(presenter)
    }

    override fun onReset() {
        super.onReset()

        presenter?.destroy()
    }
}
