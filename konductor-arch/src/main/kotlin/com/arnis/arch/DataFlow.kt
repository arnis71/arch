package com.arnis.arch

import android.util.Log
import android.view.View
import com.arnis.konductor.Controller
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/** Created by arnis on 13/12/2017 */

abstract class BaseDataFlow<T> {
    var onFlow: ((T) -> Unit)? = null

    abstract fun flow(params: Any?)

    open fun stop() {
        onFlow = null
    }
}

class DataFlow<T>(private val produce: (Any?) -> T) : BaseDataFlow<T>() {

    override fun flow(params: Any?) {
        onFlow?.invoke(produce(params)) ?: dataFlowWithoutReceiver()
    }
}

class OptionalDataFlow<T>(private val produce: (Any?) -> T?) : BaseDataFlow<T?>() {

    override fun flow(params: Any?) {
        onFlow?.invoke(produce(params)) ?: dataFlowWithoutReceiver()
    }
}

class DeferredDataFlow<T>(private val produce: suspend (Any?) -> T, private val handlerContext: CoroutineDispatcher = UI) : BaseDataFlow<T>() {
    private var job: Job? = null

    override fun flow(params: Any?) {
        job = launch(handlerContext) { onFlow?.invoke(produce(params)) ?: dataFlowWithoutReceiver() }
    }

    override fun stop() {
        job?.cancel()
        job = null
        super.stop()
    }
}

private fun dataFlowWithoutReceiver() = Log.d("ARCH", "skipping flow, no receiver")