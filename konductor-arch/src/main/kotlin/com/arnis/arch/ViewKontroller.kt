package com.arnis.arch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arnis.konductor.Controller
import com.arnis.konductor.ControllerChangeHandler
import com.arnis.konductor.RouterTransaction
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI

/** Created by arnis on 07/12/2017 */

abstract class ViewKontroller<out T: DataFlowProvider>(val dataflowProvider: T,
                                                       protected val tag: String? = null,
                                                       args: Bundle? = null) : Controller(args) {
    abstract val layout: AnkoContext<Context>.() -> Unit

    fun routeTo(kontroller: ViewKontroller<*>,
                overridePop: ControllerChangeHandler? = null,
                overridePush: ControllerChangeHandler? = null) {
        router.pushController(RouterTransaction.with(kontroller)
                .tag(kontroller.tag)
                .popChangeHandler(overridePop ?: KonductorChangeHandler.popHandler)
                .pushChangeHandler(overridePush?: KonductorChangeHandler.pushHandler))
    }

    fun returnTo(tag: String) = router.popToTag(tag)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View  {
        return container.context.UI(layout).view
    }

    override fun onDestroyView(view: View) = dataflowProvider.onDestroyView()

    override fun onDestroy() = dataflowProvider.destroy()

    protected fun <K> fromArgs(key: String) = args[key] as K
}

inline fun <reified T> ViewKontroller<*>.flow(updateOnAttach: Boolean = true,
                                              noinline update: (data: T) -> Unit) {
    (dataflowProvider.getFlow(T::class) as BaseDataFlow<T>).also {
        it.onFlow = update
        it.bindTo(this, updateOnAttach)
    }
}