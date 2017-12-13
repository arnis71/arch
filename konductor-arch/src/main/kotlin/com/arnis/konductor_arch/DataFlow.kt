package com.arnis.konductor_arch

import android.view.View

/** Created by arnis on 13/12/2017 */

typealias DataFlowProducer<T> = () -> T

abstract class BaseDataFlow<T>(private val producer: DataFlowProducer<T>) {

    var updateUi: ((data: T) -> Unit)? = null

    fun flow() = updateUi!!(producer())
}

class DataFlow<T> (produce: DataFlowProducer<T>): BaseDataFlow<T>(produce) {
    var updateOnAttach: Boolean = true

    fun bindTo(viewKontroller: ViewKontroller, updateImmediately: Boolean, updateUi: (data: T) -> Unit) {
        this.updateUi = updateUi
        if (updateImmediately)
            flow()
        viewKontroller.addLifecycleListener(object : Controller.LifecycleListener() {
            override fun postAttach(controller: Controller, view: View) {
                if (updateOnAttach) flow()
            }
            override fun preDestroyView(controller: Controller, view: View) {
                viewKontroller.removeLifecycleListener(this)
                this@DataFlow.updateUi = null
            }
        })
    }
}