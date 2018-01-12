package com.arnis.arch

import android.view.View
import com.arnis.konductor.Controller
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/** Created by arnis on 13/12/2017 */

typealias DataFlowProducer<T> = (Any?) -> T
typealias DataFlowReceiver<T> = (T) -> Unit

typealias DeferredDataFlowProducer<T> = suspend (Any?) -> T

abstract class BaseDataFlow<T> {
    var onFlow: DataFlowReceiver<T>? = null

    abstract fun flow(params: Any?)

    open fun stop() {
        onFlow = null
    }
}

class DataFlow<T>(private val produce: DataFlowProducer<T>) : BaseDataFlow<T>() {

    override fun flow(params: Any?) = onFlow?.invoke(produce(params))
            ?: dataFlowWithoutReceiver()
}

class DeferredDataFlow<T>(private val produce: DeferredDataFlowProducer<T>) : BaseDataFlow<T>() {
    private var job: Job? = null

    override fun flow(params: Any?) {
        job = launch(UI) { onFlow?.invoke(produce(params)) ?: dataFlowWithoutReceiver() }
    }

    override fun stop() {
        job?.cancel()
        job = null
        super.stop()
    }
}

fun BaseDataFlow<*>.bindTo(viewKontroller: ViewKontroller<*>, updateOnAttach: Boolean) {
    viewKontroller.addLifecycleListener(object : Controller.LifecycleListener() {
        override fun postAttach(controller: Controller, view: View) {
            if (updateOnAttach)
                flow(null)
        }
        override fun preDestroyView(controller: Controller, view: View) {
            viewKontroller.removeLifecycleListener(this)
            stop()
        }
    })
}

private fun dataFlowWithoutReceiver(): Nothing = throw Exception("data flow does not have a receiver")