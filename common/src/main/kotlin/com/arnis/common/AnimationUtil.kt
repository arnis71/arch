package com.arnis.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.HorizontalScrollView
import android.widget.ImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * Created by arnis on 26.04.17.
 */

//enum class AnimationType(val resources: Pair<Int, Int>, val offset: Long = 0) {
//    APPEAR(R.anim.appear_bottom_short to R.anim.disappear_bottom_short, 500L),
//    APPEAR_FAST(R.anim.appear_bottom_slow to -1, 50L),
//    OVERSHOOT(R.anim.appear_bottom_overshoot to R.anim.disappear_bottom_overshoot, 200L),
//    SCALE(R.anim.appear_scale to R.anim.disappear_scale, 200L)
//}
enum class AnimationPhase{
    SHOW,HIDE
}

//private fun getResourceByPhase(type: AnimationType, phase: AnimationPhase): Int {
//    return if (phase == AnimationPhase.SHOW)
//        type.resources.first
//    else if (phase == AnimationPhase.HIDE)
//        type.resources.second
//    else -1
//}
//
//fun ViewGroup.animateChildren(phase: AnimationPhase = AnimationPhase.SHOW, animationType: AnimationType){
//    forChildIndexed { child, index ->
//        if (child is ViewGroup)
//            child.animateChildren(phase,animationType)
//        else {
//            val resId = getResourceByPhase(animationType, phase)
//            child.playAnimation(resId, index * animationType.offset, phase) {}
//        }
//    }
//}

enum class Axis { X, Y, XY }

fun View.followFinger(axis: Axis, factor: Int = 1) {
    onLayout {
        setOnTouchListener(object : View.OnTouchListener {
            val initX = x
            val initY = y
            lateinit var drag: Drag
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> drag = Drag(motionEvent.rawX, motionEvent.rawY)
                    MotionEvent.ACTION_MOVE -> {
                        if (axis equal Axis.X)
                            x = initX + (motionEvent.rawX - drag.x)/factor
                        else if (axis equal Axis.X)
                            y = motionEvent.rawY / factor
                        else if (axis equal Axis.XY) {
                            x = motionEvent.rawX / factor
                            y = motionEvent.rawY / factor
                        }
                    }
                    MotionEvent.ACTION_UP -> animate().x(initX).y(initY)
                            .setInterpolator(BounceInterpolator()).setDuration(600).start()
                }
                return true
            }
        })
    }
}
private data class Drag(val x: Float, val y: Float)

fun View.animateScaleOverAnti(phase: AnimationPhase = AnimationPhase.SHOW, duration: Long = 400){
    if (phase == AnimationPhase.SHOW) {
        animate().scaleX(1f).scaleY(1f).setDuration(duration).setInterpolator(OvershootInterpolator()).start()
    } else if (phase == AnimationPhase.HIDE) {
        animate().scaleX(0f).scaleY(0f).setDuration(duration).setInterpolator(AnticipateInterpolator()).start()
    }
}

fun View.animateScale(phase: AnimationPhase = AnimationPhase.SHOW, duration: Long){
    if (phase == AnimationPhase.SHOW) {
        animate().scaleX(1f).scaleY(1f).setDuration(duration).setInterpolator(DecelerateInterpolator()).start()
    } else if (phase == AnimationPhase.HIDE) {
        animate().scaleX(0f).scaleY(0f).setDuration(duration).setInterpolator(AccelerateInterpolator()).start()
    }
}

fun View.startAnticipating() = Observable.interval(2000, TimeUnit.MILLISECONDS).subscribe {
        ui { animatePop() }
    }

fun View.animatePop() {
    animate().scaleX(1.3f).scaleY(1.3f).setDuration(250).setInterpolator(DecelerateInterpolator())
            .withEndAction {
                animate().scaleX(1f).scaleY(1f).setDuration(250).setInterpolator(OvershootInterpolator()).start()
            }.start()
}

//inline fun Spring.rebound(startValue: Double, endValue: Double, friction: Double = 5.0, tension: Double = 50.0, crossinline onSpring: (value: Double) -> Unit) {
//    springConfig.apply {
//        this.friction = friction
//        this.tension = tension
//    }
//    setCurrentValue(startValue)
//    setEndValue(endValue)
//    addListener(object : SimpleSpringListener() {
//        override fun onSpringUpdate(spring: Spring) {
//            onSpring(spring.currentValue)
//        }
//    })
//}
//
//fun HorizontalScrollView.overscrolly(resistance: Double = 1.8) {
//    isHorizontalScrollBarEnabled = false
//    overScrollMode = View.OVER_SCROLL_NEVER
//    val ss = SpringSystem.create()
//    val spring = ss.createSpring()
//    val velTrack = VelocityTracker.obtain()
//    spring.addListener(object: SimpleSpringListener() {
//        override fun onSpringUpdate(spring: Spring) {
////            log("spring ${spring.currentValue}")
//            x = spring.currentValue.toFloat()
//        }
//    })
//    setOnTouchListener(object : View.OnTouchListener {
//        var xLastValue = -1f
//        var xValue = -1f
//        var xStartValue = -1f
//        override fun onTouch(v: View, event: MotionEvent): Boolean {
////            log("left ${canScrollHorizontally(-1)} right ${canScrollHorizontally(1)}")
//            when (event.action) {
//                MotionEvent.ACTION_MOVE -> { xValue = event.rawX; velTrack.addMovement(event); if (xStartValue equal -1f) xStartValue = event.rawX }
//                MotionEvent.ACTION_UP -> { xValue = -1f; xStartValue = -1f; xLastValue = -1f }
//            }
//            if (!canScrollHorizontally(-1)/* || */) {
//
//                "dif ${xLastValue - xValue}".log
//                if (xValue != -1f && (xLastValue equal -1f || xLastValue < xValue)) {
//                    spring.currentValue = (((xValue - xStartValue)) - if (!this@overscrolly.canScrollHorizontally(1)) this@overscrolly.width else 0) / resistance
//                    return true
//                } else {
//                    "reset".log
//                    spring.endValue = 0.toDouble()
//                }
//
//            } else if (!canScrollHorizontally(1)) {
//
//            }
//            xLastValue = xValue
//
//            return false
//        }
//    })
//    viewTreeObserver.addOnScrollChangedListener( object: ViewTreeObserver.OnScrollChangedListener {
//        var xLastScroll = 0
//        override fun onScrollChanged() {
//            scrollX.let {
////                log("scrollX $it, lastScroll $xLastScroll")
////                if (it == 0 && xLastScroll != 0) {
////                    velTrack.computeCurrentVelocity(1000)
////                    spring.velocity = -velTrack.xVelocity.toDouble()
////                    log("x velocity ${velTrack.xVelocity}")
////                    velTrack.clear()
////                }
//                xLastScroll = it
//            }
//        }
//    })
//}

enum class AnimType { IN, OUT }

//fun View.animateReveal(/*mask: View? = null,*/revealX: Int = width/2, revealY: Int = height/2,  animType: AnimType = AnimType.IN, duration: Long = 400, onEnd: EmptyBlock = {}) {
////    var revealX = width/2
////    var revealY = height/2
////    mask?.let {
////        val rect = Rect().apply { it.getGlobalVisibleRect(this) }
////        revealX = rect.right + it.width/2
////        revealY = rect.bottom + it.height/2
////    }
//
//    val dx = Math.max(revealX, width - revealX).toDouble()
//    val dy = Math.max(revealY, height - revealY).toDouble()
//
//    var startRadius = 0f
//    var finalRadius = 0f
//    if (animType equal AnimType.IN)
//        finalRadius = Math.hypot(dx, dy).toFloat()
//    else if (animType equal AnimType.OUT)
//        startRadius = Math.hypot(dx, dy).toFloat()
//
//    ViewAnimationUtils.createCircularReveal(this, revealX, revealY, startRadius, finalRadius).apply {
//        interpolator = if (animType equal AnimType.OUT) AccelerateInterpolator() else DecelerateInterpolator()
//        this.duration = duration
//        addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationStart(animation: Animator?) {
//                super.onAnimationStart(animation)
//                if (animType equal AnimType.IN)
//                    visible()
//            }
//
//            override fun onAnimationEnd(animation: Animator?) {
//                super.onAnimationEnd(animation)
//                if (animType equal AnimType.OUT)
//                    invisible()
//                onEnd()
//            }
//        })
//    }.start()
//}

//fun View.animateAppearShort(phase: AnimationPhase = AnimationPhase.SHOW, onEnd: () -> Unit = {}){
//    playAnimation(getResourceByPhase(AnimationType.APPEAR,phase), phase = phase, onEnd =  onEnd)
//}

private inline fun View.playAnimation(resId: Int, offset: Long = 0, phase: AnimationPhase = AnimationPhase.SHOW, crossinline onEnd: () -> Unit){
    val animation = AnimationUtils.loadAnimation(context, resId)
    animation.startOffset = offset
    if (phase == AnimationPhase.HIDE) {
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {}
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                invisible()
                onEnd.invoke()
            }
        })
    }
    visible()
    startAnimation(animation)
}

fun View.startAnim(resId: Int) {
    startAnimation(AnimationUtils.loadAnimation(context, resId))
}

fun View.animatePress(factor:Float = 0.85f, overshoot: Boolean = true, startAction: Runnable = Runnable {}, endAction: Runnable = Runnable {}) {
    setOnTouchListener { v, event ->
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                AnimationUtil.anticipateAnim(factor, v); v.performClick() }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->  {
                if (overshoot)
                    AnimationUtil.overshootAnim(v, startAction, endAction)
                else
                    AnimationUtil.accelerateAnim(v, startAction, endAction)

            }
        }
        true
    }
}

fun Activity.animateStatusBarColor(color: Int, duration: Long = 300): ValueAnimator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        ValueAnimator.ofObject(ArgbEvaluator(), window.statusBarColor, color).apply {
            this.duration = duration
            addUpdateListener { window.statusBarColor = (it.animatedValue as Int) }
        }
    else null
}

fun View.animateToColor(color: Int, duration: Long = 300) =
    ValueAnimator.ofObject(ArgbEvaluator(), (background as ColorDrawable).color, color).apply {
    this.duration = duration
    addUpdateListener { animator -> setBackgroundColor(animator.animatedValue as Int) }
}

fun ImageView.animateOverlayIn(duration: Long = 300) {
    ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, "#60000000".color).apply {
        this.duration = duration
        addUpdateListener { animator -> setColorFilter(animator.animatedValue as Int) }
    }.start()
}

fun ImageView.animateOverlayOut(duration: Long = 300) {
    ValueAnimator.ofObject(ArgbEvaluator(), "#60000000".color, Color.TRANSPARENT).apply {
        this.duration = duration
        addUpdateListener { animator -> setColorFilter(animator.animatedValue as Int) }
    }.start()
}

fun View.animateFade(to: Float) {
    ValueAnimator.ofFloat(alpha,to).apply {
        addUpdateListener {
            alpha = it.animatedValue as Float
        }
    }.start()
}

object AnimationUtil {
    private var anticipation: Disposable? = null
//    private lateinit var gradientStack: List<View>

    fun startAnticipating(view: View?) {
        val pvhScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.3f)
        val pvhScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.3f)

        val pvhScaleXr = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.3f, 1f)
        val pvhScaleYr = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.3f, 1f)

        val scale = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY)
        scale.interpolator = DecelerateInterpolator()
        scale.duration = 300

        val scaleR = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleXr, pvhScaleYr)
        scaleR.interpolator = OvershootInterpolator()
        scaleR.duration = 400

        val set = AnimatorSet()
        set.playSequentially(scale, scaleR)

        anticipation = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .filter { it % 4 == 0L }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { set.start() }
    }

    fun stopAnticipating() {
        if (anticipation != null)
            anticipation!!.dispose()
    }

    @Deprecated("Use appearAnim instead")
    fun appearV1(animation: android.view.animation.Animation, vararg views: View) {
        for (i in views.indices) {
            views[i].animation = animation
            views[i].animation.startOffset = (i * 150).toLong()
            views[i].animation.start()
        }
    }

    fun startRingSpinners(innerSpinner: Pair<View, android.view.animation.Animation>, outerSpinner: Pair<View, android.view.animation.Animation>) {
        val (inView,inAnim) = innerSpinner
        val (outView,outAnim) = outerSpinner
        inView.visibility = View.VISIBLE
        outView.visibility = View.VISIBLE

        inView.startAnimation(inAnim)
        outView.startAnimation(outAnim)
    }

    fun crossfade(view1: View, view2: View, reverseDelay: Long = 0) {
        if (view1.alpha == 1f) {
            view1.animate().alpha(0f).setDuration(500).start()
            view2.animate().alpha(1f).setDuration(500).start()
        } else if (view1.alpha == 0f) {
            view1.animate().alpha(1f).setStartDelay(reverseDelay).setDuration(500).start()
            view2.animate().alpha(0f).setStartDelay(reverseDelay).setDuration(500).start()
        }
    }

    private fun crossFadeViews(view1: View, view2: View, direction: Int) {
        if (direction < 0) {
            if (view1.alpha == 0f)
                view1.animate().alpha(1f).setDuration(800).withEndAction { view2.alpha = 0f }.start()
            else
                view2.animate().alpha(1f).setDuration(800).withEndAction { view1.alpha = 0f }.start()
        } else if (direction > 0) {
            view2.alpha = 1f
            view1.animate().alpha(0f).setDuration(800).start()
        }
    }

    fun toggleView(viewToggle: List<View>, from: Int, to: Int) {
        if (from != to)
            crossFadeViews(viewToggle[from], viewToggle[to], from - to)
    }

    internal fun anticipateAnim(factor: Float, view: View) {
        view.playSoundEffect(SoundEffectConstants.CLICK)
        view.animate().scaleX(factor).scaleY(factor).setDuration(300).setInterpolator(DecelerateInterpolator()).start()
    }

    internal fun overshootAnim(view: View, startAction: Runnable?, endAction: Runnable?) {
        view.animate().scaleX(1f).scaleY(1f).setDuration(400).withStartAction(startAction).withEndAction(endAction).setInterpolator(OvershootInterpolator()).start()
    }

    internal fun accelerateAnim(view: View, startAction: Runnable?, endAction: Runnable?) {
        view.animate().scaleX(1f).scaleY(1f).setDuration(600).withStartAction(startAction).withEndAction(endAction).setInterpolator(AccelerateInterpolator()).start()
    }

//    internal val springQueue = arrayListOf<SpringAnimation>()
//
//    internal fun <T> newSpring(target: T, property: FloatPropertyCompat<T>, finalValue: Float, updateListener: DynamicAnimation.OnAnimationUpdateListener? = null, endListener: DynamicAnimation.OnAnimationEndListener? = null){
//        val anim = SpringAnimation(target,property)
//        anim.addUpdateListener(updateListener)
//        anim.addEndListener(endListener)
//
//        val spring = SpringForce(finalValue)
//        spring.apply {
//            dampingRatio = DAMPING_RATIO_MEDIUM_BOUNCY
//            stiffness = STIFFNESS_LOW
//        }
//
//        anim.spring = spring
//
//        springQueue.add(anim)
//    }
}

    //    public void hideChatButtons(View buttonsLayout, long delay) {
    //        buttonsLayout.animate().y(screenHeight+buttonsLayout.getHeight()).setDuration(400).setStartDelay(delay).setInterpolator(new AccelerateInterpolator()).start();
    //    }
    //
    //    public void showChatButtons(View buttonsLayout) {
    //        buttonsLayout.animate().y(chatButtonsStartY).setDuration(400).setInterpolator(new OvershootInterpolator()).start();
    //    }
    //
    //    public void moveAndReveal(View circle, final View revealLayout){
    //        circle.setEnabled(false);
    //        ObjectAnimator translationAnimation = ObjectAnimator.ofFloat(circle,View.Y ,circle.getY(), parentHeight /2 - (circle.getHeight()/2));
    //        translationAnimation.setInterpolator(new OvershootInterpolator());
    //        translationAnimation.setDuration(500);
    //
    //        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,1f,0f);
    //        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,1f,0f);
    //        ObjectAnimator scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(circle,pvhScaleX,pvhScaleY);
    //        scaleAnimation.setInterpolator(new AccelerateInterpolator());
    //        scaleAnimation.setDuration(400);
    //
    //        int cx = (revealLayout.getLeft() + revealLayout.getRight()) / 2;
    //        int cy = (revealLayout.getTop() + revealLayout.getBottom()) / 2;
    //
    //        // fields the final radius for the clipping ring
    //        int dx = Math.max(cx, revealLayout.getWidth() - cx);
    //        int dy = Math.max(cy, revealLayout.getHeight() - cy);
    //        float finalRadius = (float) Math.hypot(dx, dy);
    //
    //        // Android native animator
    //        Animator revealAnimation = ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, 0, finalRadius);
    //        revealAnimation.setInterpolator(new AccelerateInterpolator());
    //        revealAnimation.setDuration(400);
    //        revealAnimation.addListener(new AnimatorListenerAdapter() {
    //            @Override
    //            public void onAnimationStart(Animator animation) {
    //                revealLayout.setVisibility(View.VISIBLE);
    //            }
    //
    //            @Override
    //            public void onAnimationEnd(Animator animation) {
    //                socialOpen = true;
    //            }
    //        });
    //
    //        AnimatorSet set = new AnimatorSet();
    //        set.playSequentially(translationAnimation,scaleAnimation,revealAnimation);
    //        set.start();
    //    }
    //
    //    public void reverseMoveAndReveal(View circle, final View revealLayout){
    //        ObjectAnimator translationAnimation = ObjectAnimator.ofFloat(circle,View.Y ,circle.getY(), circleStartY);
    //        translationAnimation.setInterpolator(new OvershootInterpolator());
    //        translationAnimation.setDuration(500);
    //
    //        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,0f,1f);
    //        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,0f,1f);
    //        ObjectAnimator scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(circle,pvhScaleX,pvhScaleY);
    //        scaleAnimation.setInterpolator(new OvershootInterpolator());
    //        scaleAnimation.setDuration(500);
    //
    //        int cx = (revealLayout.getLeft() + revealLayout.getRight()) / 2;
    //        int cy = (revealLayout.getTop() + revealLayout.getBottom()) / 2;
    //
    //        // fields the final radius for the clipping ring
    //        int dx = Math.max(cx, revealLayout.getWidth() - cx);
    //        int dy = Math.max(cy, revealLayout.getHeight() - cy);
    //        float finalRadius = (float) Math.hypot(dx, dy);
    //
    //        // Android native animator
    //        Animator revealAnimation = ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, finalRadius, 0);
    //        revealAnimation.setInterpolator(new AccelerateInterpolator());
    //        revealAnimation.setDuration(500);
    //        revealAnimation.addListener(new AnimatorListenerAdapter() {
    //            @Override
    //            public void onAnimationEnd(Animator animation) {
    //                revealLayout.setVisibility(View.INVISIBLE);
    //                socialOpen = false;
    //                circle.setEnabled(true);
    //            }
    //        });
    //
    //        AnimatorSet set = new AnimatorSet();
    //        set.playSequentially(revealAnimation,scaleAnimation,translationAnimation);
    //        set.start();
    //    }
    //
    //    public void showMenuActions(View... targets){
    //        float circleCenter = circleLeft + (density * 20);
    //        AnimatorSet set = new AnimatorSet();
    //        set.playTogether(
    //                move(targets[0],circleCenter,circleTop, parentWidth / 2 - (targets[0].getWidth()/2) - (density * 70),circleTop - (density * 130),0),
    ////            move(targets[1],circleCenter,circleTop, parentWidth / 2 - (targets[1].getWidth()/2),circleTop - (density * 130),100),
    //                move(targets[1],circleCenter,circleTop, parentWidth / 2 - (targets[1].getWidth()/2) + (density * 70),circleTop - (density * 130),200)
    //        );
    //
    //        set.start();
    //    }
    //
    //    public void hideMenuActions(final Runnable actionRunnable, View... targets){
    //        float circleCenter = circleLeft + (density * 20);
    //        AnimatorSet set = new AnimatorSet();
    //        set.playTogether(
    //                hide(targets[0],circleCenter,circleTop, parentWidth / 2 - (targets[0].getWidth()/2) - (density * 70),circleTop - (density * 130),0),
    ////                hide(targets[1],circleCenter,circleTop, parentWidth / 2 - (targets[1].getWidth()/2),circleTop - (density * 130),100),
    //                hide(targets[1],circleCenter,circleTop, parentWidth / 2 - (targets[1].getWidth()/2) + (density * 70),circleTop - (density * 130),200)
    //        );
    //        set.addListener(new AnimatorListenerAdapter() {
    //            @Override
    //            public void onAnimationEnd(Animator animation) {
    //                if (actionRunnable!=null)
    //                    actionRunnable.run();
    //            }
    //        });
    //        set.start();
    //    }
    //
    //    public void setChatButtonsStartY(final View chatButtons){
    //        chatButtons.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
    //            @Override
    //            public void onGlobalLayout() {
    //                chatButtonsStartY = chatButtons.getY();
    //
    //                chatButtons.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    //            }
    //        });
    //
    //    private ObjectAnimator move(View view, float startX, float startY, float destX, float destY, long offset){
    //        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,startX, destX);
    //        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,startY, destY);
    //        PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA,0, 1);
    //
    //        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,pvhX,pvhY,pvhA);
    //        view.setVisibility(View.VISIBLE);
    //        animator.setDuration(500).setStartDelay(offset);
    //        animator.setInterpolator(new OvershootInterpolator());
    //        return animator;
    //    }
    //
    //    private ObjectAnimator hide(final View view, float destX, float destY, float startX, float startY, long offset){
    //        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,startX, destX);
    //        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,startY, destY);
    //        PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA,1, 0);
    //
    //        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view,pvhX,pvhY,pvhA);
    //        animator.setDuration(300).setStartDelay(offset);
    //        animator.addListener(new AnimatorListenerAdapter() {
    //            @Override
    //            public void onAnimationEnd(Animator animation) {
    //                view.setVisibility(View.INVISIBLE);
    //            }
    //        });
    //        animator.setInterpolator(new AnticipateInterpolator());
    //        return animator;
    //    }
    //
    //    public void setViewParentDimen(final View parentView, DisplayMetrics displayMetrics) {
    //        this.density = displayMetrics.density;
    //        this.screenHeight = displayMetrics.heightPixels;
    //
    //        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
    //            @Override
    //            public void onGlobalLayout() {
    //                parentWidth = parentView.getWidth();
    //                parentHeight = parentView.getHeight();
    //
    //                parentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    //            }
    //        });
    //    }
    //
    //    public void setCircleDimen(final View circle){
    //        circle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
    //            @Override
    //            public void onGlobalLayout() {
    //                circleStartY = circle.getY();
    //                circleLeft = circle.getLeft();
    //                circleTop = circle.getTop();
    //
    //                circle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    //            }
    //        });
    //    }

