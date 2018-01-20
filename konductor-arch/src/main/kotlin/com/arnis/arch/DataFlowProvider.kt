package com.arnis.arch

import android.util.ArrayMap
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlin.reflect.KClass

/** Created by arnis on 13/12/2017 */

abstract class DataFlowProvider {
    protected val providers: ArrayMap<KClass<*>, BaseDataFlow<*>> = ArrayMap()
    private var compositeDisposable = CompositeDisposable()
    private var usesRx = false

    protected inline fun <reified T> addDataFlow(noinline producer: (Any?) -> T) {
        providers[T::class] = DataFlow(producer)
    }

    protected inline fun <reified T> addDeferredDataFlow(handlerContext: CoroutineDispatcher,
                                                         noinline producer: suspend (Any?) -> T) {
        providers[T::class] = DeferredDataFlow(handlerContext, producer)
    }

    fun getFlow(clazz: KClass<*>): BaseDataFlow<*>? {
        return providers[clazz]
    }

    protected inline fun <reified T> forceFlow(params: Any? = null) {
        getFlow(T::class)?.flow(params)
                ?: Log.d("ARCH", "can not forceFlow, no flow for class ${T::class}")
    }

    fun manageDisposable(disposable: Disposable) {
        usesRx = true
        compositeDisposable.add(disposable)
    }

    internal fun destroyView() {
        if (usesRx) {
            compositeDisposable.dispose()
            compositeDisposable.clear()
            compositeDisposable = CompositeDisposable()
        }
        onDestroyView()
    }

    internal fun destroy() {
        providers.clear()
        onDestroy()
    }

    open fun onDestroy() {}
    open fun onDestroyView() {}
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