package com.arnis.konductor_arch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/** Created by arnis on 07/12/2017 */

abstract class ViewKontroller: Controller() {
    abstract val layout: AnkoContext<Context>.() -> Unit
    abstract val abstraction: Abstraction

    fun routeTo(screen: String) = (activity as KonductorActivity).konductor.let {
        router.pushController(RouterTransaction.with(it.route(screen))
                .popChangeHandler(it.popHandler)
                .pushChangeHandler(it.pushHandler))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View  {
        return container.context.UI(layout).view
    }

    override fun onDestroy() = abstraction.clear()
}

inline fun <reified T> ViewKontroller.bind(updateOnAttach: Boolean = true,
                                           updateImmediately: Boolean = false,
                                           noinline updateUi: (data: T) -> Unit): DataFlow<T> {
    return abstraction.get<T>().also {
        it.updateOnAttach = updateOnAttach
        it.bindTo(this, updateImmediately, updateUi)
    }
}