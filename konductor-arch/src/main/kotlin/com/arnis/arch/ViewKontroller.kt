package com.arnis.arch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.RouterTransaction
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.UI
import kotlin.reflect.KClass

/** Created by arnis on 07/12/2017 */

abstract class ViewKontroller(args: Bundle? = null) : Controller(args) {
    private var provider: DataFlowProvider? = null

    abstract val tag: String
    abstract fun AnkoContext<Context>.onLayout()

    fun <T: DataFlowProvider> bindProvider(clazz: KClass<T>, run: T.() -> Unit = {}) {
        DataFlowFactory.get(clazz).also {
            it.run()
            provider = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View  {
        provider?.attach(tag)
        return container.context.UI { onLayout() }.view
    }

    override fun onDestroyView(view: View) {
        provider?.detach(this)
    }

    override fun onDestroy() {
        provider = null
    }

    fun getFlowByClass(clazz: KClass<*>) = provider?.getFlow(clazz)
            ?: throw Exception("can not flow, no provider")

    fun routeTo(kontroller: ViewKontroller,
                overridePop: ControllerChangeHandler? = null,
                overridePush: ControllerChangeHandler? = null) {
        router.pushController(
            RouterTransaction.with(kontroller)
                .tag(kontroller.tag)
                .popChangeHandler(overridePop ?: KonductorChangeHandler.popHandler)
                .pushChangeHandler(overridePush?: KonductorChangeHandler.pushHandler))
    }

    fun returnTo(tag: String) = router.popToTag(tag)

    fun routeBack() = router.popCurrentController()

    fun ViewGroup.child(kontroller: ViewKontroller,
                        overridePop: ControllerChangeHandler? = null,
                        overridePush: ControllerChangeHandler? = null) {
        getChildRouter(this).setPopsLastView(false).run {
            if (!hasRootController())
                setRoot(RouterTransaction.with(kontroller)
                    .tag(kontroller.tag)
                    .popChangeHandler(overridePop ?: KonductorChangeHandler.popHandler)
                    .pushChangeHandler(overridePush?: KonductorChangeHandler.pushHandler))
        }
    }
}

inline fun <reified T> ViewKontroller.flow(invokeFlow: Boolean = true,
                                              noinline receiver: (data: T) -> Unit) {
    (getFlowByClass(T::class) as BaseDataFlow<T>).apply {
        attachToKontroller(this@flow, receiver)
        if (invokeFlow)
            flow(null)
    }
}