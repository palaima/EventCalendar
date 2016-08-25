package io.palaima.calendar.ui.controller.base

import android.app.Activity
import android.app.LoaderManager
import android.content.Loader
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View
import timber.log.Timber

abstract class PresenterController<V: MvpView, P: MvpPresenter<V>>(args: Bundle? = null): ButterKnifeController(args),
        LoaderManager.LoaderCallbacks<P> {

    private val LOADER_ID = 1

    private lateinit var presenter: P

    // Boolean flag to avoid delivering the MvpPresenter twice. Calling initLoader in onActivityCreated means
    // onLoadFinished will be called twice during configuration change.
    private var delivered = false

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

    override fun onActivityStarted(activity: Activity?) {
        super.onActivityStarted(activity)
        initLoader()
    }

    override fun onLoadFinished(loader: Loader<P>?, newPresenter: P) {
        Timber.d("onLoadFinished " + newPresenter)
        if (!delivered) {
            presenter = newPresenter
            delivered = true
        }
    }

    override fun onLoaderReset(loader: Loader<P>?) {
        presenter.destroy()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<P> {
        return PresenterLoader(activity, { createPresenter() })
    }

    fun presenter(): P {
        return presenter
    }

    private fun getViewLayer(): V {
        @Suppress("UNCHECKED_CAST")
        return this as V
    }

    private fun initLoader() {
        //activity.loaderManager.initLoader<P>(LOADER_ID, null, this)
    }

    private fun setPresenter() {
        presenter = createPresenter()
        Timber.d("setPresenter " + presenter)
    }
}
