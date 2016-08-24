package io.palaima.calendar.ui.controller.base

import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.rxlifecycle.RxController

abstract class ButterKnifeController(args: Bundle? = null): RxController(args) {

    private lateinit var unbinder: Unbinder

    protected abstract fun inflateView(inflater: LayoutInflater, container: ViewGroup): View

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflateView(inflater, container)
        unbinder = ButterKnife.bind(this, view)
        bind(view)
        return view
    }

    protected open fun bind(view: View) { }

    protected open fun unbind(view: View) { }

    @CallSuper
    override fun onDestroyView(view: View) {
        unbind(view)
        super.onDestroyView(view)
        unbinder.unbind()
    }
}
