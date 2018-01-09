package com.arnis.arch

import android.os.Bundle
import android.view.View
import com.arnis.konductor.Controller

/** Created by arnis on 13/12/2017 */

typealias DataFlowProducer<T> = (Any?) -> T
typealias DataFlowReceiver<T> = (T) -> Unit

abstract class BaseDataFlow<out T>(private val producer: DataFlowProducer<T>) {

    private var update: DataFlowReceiver<T>? = null

    fun onFlow(dataFlowReceiver: DataFlowReceiver<T>) {
        update = dataFlowReceiver
    }

    fun flow(params: Any? = null) = update!!(producer(params))

    internal fun stop() {
        update = null
    }
}

class KontrollerDataFlow<out T> (produce: DataFlowProducer<T>): BaseDataFlow<T>(produce) {

    var updateOnAttach: Boolean = true

    fun bindTo(viewKontroller: ViewKontroller<*>) {
        viewKontroller.addLifecycleListener(object : Controller.LifecycleListener() {
            override fun postAttach(controller: Controller, view: View) {
                attach()
            }
            override fun preDestroyView(controller: Controller, view: View) {
                viewKontroller.removeLifecycleListener(this)
                stop()
            }
        })
    }

    internal fun attach() {
        if (updateOnAttach) flow()
    }
}

//class DataFlowLifeCycle(val dataFlow: DataFlow<*>) {
//    fun init() {}
//    fun destroy() {}
//}