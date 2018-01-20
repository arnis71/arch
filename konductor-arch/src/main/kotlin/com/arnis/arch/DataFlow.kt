package com.arnis.arch

import android.util.Log
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

/** Created by arnis on 13/12/2017 */

abstract class BaseDataFlow<T> {
    var receiver: ((T) -> Unit)? = null

    abstract fun flow(params: Any?)

    open fun stop() {
        receiver = null
    }
}

open class DataFlow<T>(private val producer: (Any?) -> T) : BaseDataFlow<T>() {

    override fun flow(params: Any?) {
        receiver?.invoke(producer(params)) ?: dataFlowWithoutReceiver()
    }
}

class DeferredDataFlow<T>(private val handlerContext: CoroutineDispatcher,
                          private val producer: suspend (Any?) -> T) : BaseDataFlow<T>() {
    private var job: Job? = null

    override fun flow(params: Any?) {
        job = launch(handlerContext) {
            receiver?.invoke(producer(params)) ?: dataFlowWithoutReceiver()
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
        super.stop()
    }
}

private fun dataFlowWithoutReceiver() = Log.d("ARCH", "skipping flow, no receiver")