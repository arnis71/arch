package com.arnis.arch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnis.konductor.Controller
import com.arnis.konductor.ControllerChangeHandler
import com.arnis.konductor.RouterTransaction
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.Deferred
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/** Created by arnis on 07/12/2017 */

typealias Params = (Bundle.() -> Unit)

abstract class ViewKontroller<out T: Abstraction>(val abstraction: T, withParams: Params? = null) : Controller(withParams?.let { Bundle().apply(it) }) {
    abstract val layout: AnkoContext<Context>.() -> Unit

    fun routeTo(kontroller: ViewKontroller<*>,
                overridePop: ControllerChangeHandler? = null,
                overridePush: ControllerChangeHandler? = null)
            = (activity as KonductorActivity).changeHandler.let {
        router.pushController(RouterTransaction.with(kontroller)
                .popChangeHandler(overridePop ?: it.popHandler)
                .pushChangeHandler(overridePush?: it.pushHandler))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View  {
        return container.context.UI(layout).view
    }

    override fun onDestroyView(view: View) = abstraction.destroyView()

    override fun onDestroy() = abstraction.destroy()

    protected fun <K> fromArgs(key: String) = args[key] as K
}

inline fun <reified T> ViewKontroller<*>.bind(updateOnAttach: Boolean = true,
                                           noinline update: (data: T) -> Unit) {
    (abstraction.get<T>() as BaseDataFlow<T>).also {
        it.onFlow = update
        it.bindTo(this, updateOnAttach)
    }
}

inline fun <reified T> ViewKontroller<*>.bindDeferred(updateOnAttach: Boolean = true,
                                              noinline update: (data: T) -> Unit) {
    (abstraction.get<Deferred<T>>() as DeferredDataFlow<T>).also {
        it.onFlow = update
        it.bindTo(this, updateOnAttach)
    }
}