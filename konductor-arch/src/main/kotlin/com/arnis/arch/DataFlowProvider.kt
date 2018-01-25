package com.arnis.arch

import android.util.ArrayMap
import android.util.Log
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlin.reflect.KClass

/** Created by arnis on 13/12/2017 */

abstract class DataFlowProvider {
    protected val providers: ArrayMap<KClass<*>, BaseDataFlow<*>> = ArrayMap()
    private var currentViewKontroller: String = ""

    protected inline fun <reified T> addDataFlow(noinline producer: (Any?) -> T) {
        providers[T::class] = DataFlow(producer)
    }

    protected inline fun <reified T> addDeferredDataFlow(handlerContext: CoroutineDispatcher,
                                                         noinline producer: suspend (Any?) -> T) {
        providers[T::class] = DeferredDataFlow(handlerContext, producer)
    }

    protected inline fun <reified T> addDirectDataFlow() {
        providers[T::class] = DirectDataFlow<T>()
    }

    internal fun attach(kontrollerTag: String) {
        currentViewKontroller = kontrollerTag
        onAttach(kontrollerTag)
    }
    internal fun detach(viewKontroller: ViewKontroller) {
        if (currentViewKontroller == viewKontroller.tag) {
            providers.removeAll(providers.filter {
                it.value.isOwningKontroller(viewKontroller).apply {
                    if(this)
                        it.value.detachFromKontroller(viewKontroller)
                }
            }.map { it.key })
            onDetach(viewKontroller.tag)
        } else
            Log.w("ARCH", "skipping detach from ${viewKontroller.tag}, attached to $currentViewKontroller")
    }

    open fun onAttach(kontrollerTag: String) {}
    open fun onDetach(kontrollerTag: String) {}

    fun getFlow(clazz: KClass<*>): BaseDataFlow<*> {
        return providers[clazz] ?: throw Exception("can not flow, no provider for $clazz")
    }

    protected inline fun <reified T> directFlow(value: T) {
        (getFlow(T::class) as DirectDataFlow<T>).directFlow(value)
    }

    protected inline fun <reified T> forceFlow(params: Any? = null) {
        getFlow(T::class).flow(params)
    }
}

//    open fun handle(viewId: Int) {}

//    infix fun clicks(view: View) {
//        view.setOnClickListener {
//            handle(view.id)
//        }
//    }
//
//    infix fun presses(view: View) {
//        view.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    view.playSoundEffect(SoundEffectConstants.CLICK)
//                    view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(300).setInterpolator(DecelerateInterpolator()).start()
//                    v.performClick() }
//                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->  {
//                    view.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction {
//                        handle(view.id)
//                    }.setInterpolator(AccelerateInterpolator()).start()
//                }
//            }
//            true
//        }
//    }