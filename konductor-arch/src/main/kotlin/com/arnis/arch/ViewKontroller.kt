package com.arnis.arch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnis.konductor.Controller
import com.arnis.konductor.ControllerChangeHandler
import com.arnis.konductor.RouterTransaction
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/** Created by arnis on 07/12/2017 */

abstract class ViewKontroller<T: Abstraction>: Controller() {
    abstract val layout: AnkoContext<Context>.(abstraction: T) -> Unit
    val abstraction: T
        get() = (activity as KonductorActivity).abstractions(this::class) as T

    fun routeTo(screen: String,
                overridePop: ControllerChangeHandler? = null,
                overridePush: ControllerChangeHandler? = null)
            = (activity as KonductorActivity).changeHandler.let {
        router.pushController(RouterTransaction.with(it.route(screen))
                .popChangeHandler(overridePop ?: it.popHandler)
                .pushChangeHandler(overridePush?: it.pushHandler))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View  {
        return container.context.UI { layout(abstraction) }.view
    }

    override fun onDestroy() = abstraction.clear()
}

inline fun <reified T> ViewKontroller<*>.bind(updateOnAttach: Boolean = true,
                                           updateImmediately: Boolean = false,
                                           noinline update: (data: T) -> Unit): KontrollerDataFlow<T> {
    return abstraction.get<T>().also {
        it.updateOnAttach = updateOnAttach
        it.onFlow(update)
        it.bindTo(this)
    }
}