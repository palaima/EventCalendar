package io.palaima.calendar.ui.controller.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import timber.log.Timber

abstract class PresenterController<V: MvpView, out P: MvpPresenter<V>>(args: Bundle? = null): ButterKnifeController(args) {

    private lateinit var presenter: P

    init {
        lifecycle()
            .subscribe { event ->
                Timber.d(event.toString())
            }

        setPresenter()
    }

    abstract fun createPresenter(): P

    @CallSuper
    override fun bind(view: View) {
        super.bind(view)
        presenter.bind(getViewLayer())
    }

    @CallSuper
    override fun unbind(view: View) {
        super.unbind(view)
        presenter.unbind(getViewLayer())
    }

    @CallSuper
    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    fun presenter(): P {
        return presenter
    }

    private fun getViewLayer(): V {
        @Suppress("UNCHECKED_CAST")
        return this as V
    }

    private fun setPresenter() {
        presenter = createPresenter()
        Timber.d("setPresenter " + presenter)
    }
}
