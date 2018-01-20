package com.arnis.arch

import android.util.Log
import android.view.View
import com.arnis.konductor.Controller
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

/** Created by arnis on 13/12/2017 */

abstract class BaseDataFlow<T> {
    private val owningKontrollerTags = arrayListOf<String>()

    internal var receiver: ((T) -> Unit)? = null

    fun attachToKontroller(kontroller: ViewKontroller, receiver: (T) -> Unit) {
        owningKontrollerTags.add(kontroller.tag)
        this.receiver = receiver
    }

    fun detachFromKontroller(kontroller: ViewKontroller) {
        owningKontrollerTags.remove(kontroller.tag)
        stop()
    }

    internal fun isOwningKontroller(viewKontroller: ViewKontroller)
            = owningKontrollerTags.contains(viewKontroller.tag)

    abstract fun flow(params: Any?)

    open fun stop() {
        receiver = null
    }
}

open class DataFlow<T>(producer: (Any?) -> T) : BaseDataFlow<T>() {
    private var producer: ((Any?) -> T)? = producer

    override fun flow(params: Any?) {
        receiver?.invoke(producer!!(params)) ?: dataFlowWithoutReceiver()
    }

    override fun stop() {
        super.stop()
        producer = null
    }
}

class DeferredDataFlow<T>(private val handlerContext: CoroutineDispatcher,
                          producer: suspend (Any?) -> T) : BaseDataFlow<T>() {
    private var producer: (suspend (Any?) -> T)? = producer
    private var job: Job? = null

    override fun flow(params: Any?) {
        job = launch(handlerContext) {
            receiver?.invoke(producer!!(params)) ?: dataFlowWithoutReceiver()
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
        producer = null
        super.stop()
    }
}

private fun dataFlowWithoutReceiver() = Log.d("ARCH", "skipping flow, no receiver")