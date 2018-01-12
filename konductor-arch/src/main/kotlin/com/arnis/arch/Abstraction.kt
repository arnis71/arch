package com.arnis.arch

import android.util.ArrayMap
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.reflect.KClass

/** Created by arnis on 13/12/2017 */

abstract class Abstraction {
    val providers: ArrayMap<KClass<*>, BaseDataFlow<*>> = ArrayMap()
    private var compositeDisposable = CompositeDisposable()
    private var usesRx = false

    inline fun <reified T> register(dataFlow: BaseDataFlow<T>)
            = providers.put(T::class, dataFlow)

    inline fun <reified T> get(): BaseDataFlow<*> {
        return providers.asSequence().filter {
            it.key == T::class
        }.first().value
    }

    abstract fun handle(viewId: Int)

    fun manageDisposable(disposable: Disposable) {
        usesRx = true
        compositeDisposable.add(disposable)
    }

    infix fun clicks(view: View) {
        view.setOnClickListener {
            handle(view.id)
        }
    }

    infix fun presses(view: View) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                    view.animate().scaleX(0.85f).scaleY(0.85f).setDuration(300).setInterpolator(DecelerateInterpolator()).start()
                    v.performClick() }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->  {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(600).withEndAction {
                        handle(view.id)
                    }.setInterpolator(AccelerateInterpolator()).start()
                }
            }
            true
        }
    }

    inline fun <reified T> dispatch(params: Any? = null)
            = providers.asSequence().find { it.key == T::class }!!.value.flow(params)

    internal fun destroyView() {
        if (usesRx) {
            compositeDisposable.dispose()
            compositeDisposable.clear()
            compositeDisposable = CompositeDisposable()
        }
    }

    open fun destroy() = providers.clear()
}