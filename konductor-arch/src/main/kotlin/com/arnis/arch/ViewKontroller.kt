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
import org.jetbrains.anko.relativeLayout
import kotlin.reflect.KClass

/** Created by arnis on 07/12/2017 */

abstract class ViewKontroller<in T: DataFlowProvider>(dataflowProvider: T,
                                                      private val tag: String? = null,
                                                      args: Bundle? = null) : Controller(args) {
    private var dataflowProvider: T? = dataflowProvider

    abstract fun AnkoContext<Context>.layout(dataFlowProvider: T)

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
        return container.context.UI { layout(dataflowProvider!!) }.view
    }

    override fun onDestroyView(view: View) = dataflowProvider!!.onDestroyView()

    override fun onDestroy() {
        dataflowProvider!!.destroy()
        dataflowProvider = null
    }

    protected fun <K> fromArgs(key: String) = args[key] as K

    fun getFlowByClass(clazz: KClass<*>) = dataflowProvider!!.getFlow(clazz)
}

inline fun <reified T> ViewKontroller<*>.flow(updateOnAttach: Boolean = true,
                                              noinline update: (data: T) -> Unit) {
    (getFlowByClass(T::class) as BaseDataFlow<T>).also {
        it.onFlow = update
        it.bindTo(this, updateOnAttach)
    }
}