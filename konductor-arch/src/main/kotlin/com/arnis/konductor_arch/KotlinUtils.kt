package com.arnis.konductor_arch

import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.usage.UsageStatsManager
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Base64
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.activityManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

// VIEW EXTENSIONS
fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { findViewById<T>(idRes) }
}

fun <T : View> View.bind(@IdRes idRes: Int): Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { findViewById<T>(idRes) }
}

fun Activity.bindString(@IdRes idRes: Int): Lazy<String> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { string(idRes) as String }
}

fun View.bindString(@IdRes idRes: Int): Lazy<String> {
    @Suppress("UNCHECKED_CAST")
    return unsafeLazy { resources.getString(idRes) as String }
}

fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

fun View.inflate(resource: Int): View {
    return LayoutInflater.from(context).inflate(resource, null)
}

fun Context.inflateView(resource: Int): View {
    return LayoutInflater.from(this).inflate(resource, null)
}

fun ViewGroup.inflateLayout(resource: Int): View {
    return LayoutInflater.from(context).inflate(resource, this, false)
}

operator fun View.get(id: Int) = findViewById<View>(id)

fun <T : View> T.addTo(parent: ViewGroup) = apply { parent.addView(this) }

@Suppress("UNCHECKED_CAST")
inline fun <T : View> ViewGroup.forChild(block: (child: T) -> Unit) {
    if (childCount > 0)
        for (i in 0..childCount - 1)
            block(getChildAt(i) as T)
}

inline fun ViewGroup.forChildIndexed(code: (child: View, index: Int) -> Unit) {
    if (childCount > 0)
        for (i in 0..childCount - 1)
            code.invoke(getChildAt(i), i)
}

@Suppress("UNCHECKED_CAST")
operator fun <T : View> ViewGroup.get(index: Int): T? {
    getChildAt(index)?.let {
        return it as T
    }

    return null
}

inline fun View.onFling(crossinline action: (direction: String) -> Unit) {
    val gd = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent, v1: Float, v2: Float): Boolean {
            action(e1?.getSlope(e2).orEmpty())
            return true
        }
    })
    setOnTouchListener { _, motionEvent -> gd.onTouchEvent(motionEvent); true }
}

fun MotionEvent.getSlope(otherEvent: MotionEvent) = when (Math.toDegrees(Math.atan2((y - otherEvent.y).toDouble(), (otherEvent.x - x).toDouble()))) {
    in 46..135 -> "up"
    in 135..180 -> "left"
    in -44..45 -> "right"
    in -134..-46 -> "down"
    in -180..-135 -> "left"
    else -> ""
}

val <T : View> T.weakReference: WeakReference<T>
    get() = WeakReference(this)

@TargetApi(19)
fun <K, V> arrayMapOf() = ArrayMap<K, V>()

@TargetApi(19)
fun <K, V> arrayMapOf(vararg values: Pair<K, V>) = ArrayMap<K, V>().apply {
    values.forEach { (key, value) -> put(key, value) }
}

//fun View.centerIn(){
//    val lp = RelativeLayout.LayoutParams() as RelativeLayout.LayoutParams
//    lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//    layoutParams = lp
//}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun ImageView.setImageByteArray(byteArray: ByteArray, scaleX: Int = 0, scaleY: Int = 0) {
    var bitmap = byteArray.toBitmap()
    if (scaleX != 0 || scaleY != 0)
        bitmap = Bitmap.createScaledBitmap(bitmap, scaleX, scaleY, false)

    setImageBitmap(bitmap)
}

fun ByteArray.toBitmap() = BitmapFactory.decodeByteArray(this, 0, this.size)

fun ByteArray.toBase64() = Base64.encodeToString(this, Base64.DEFAULT)

fun Drawable.toByteArray(): ByteArray {
    safeFrom<ClassCastException> {
        return (this as BitmapDrawable).bitmap.toByteArray()
    }
    return byteArrayOf()
}

fun Bitmap.toByteArray(): ByteArray {
    safeFrom<ClassCastException> {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
    return byteArrayOf()
}

fun Bitmap.replaceTransparent(color: Int) = Bitmap.createBitmap(width, height, config).apply {
    eraseColor(color)
    Canvas(this).drawBitmap(this@replaceTransparent, 0f, 0f, null)
}

//inline fun Bitmap.getPallette(crossinline onReady: (palette: Palette) -> Unit) = bg {
//    ui { onReady(Palette.from(this@getPallette).generate()) }
//}

fun Bitmap.cropCircle(): Bitmap {
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val paint = Paint()
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)

    paint.setAntiAlias(true)
    canvas.drawARGB(0, 0, 0, 0)
    paint.setColor(Color.BLACK)
    canvas.drawOval(rectF, paint)

    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
    canvas.drawBitmap(this, rect, rect, paint)

    recycle()

    return output
}

fun View.getBitmap(): Bitmap {
    buildDrawingCache()
    return drawingCache
}

fun Context.drawable(resId: Int) = ContextCompat.getDrawable(this, resId)!!

fun Context.bitmapFromRes(resId: Int) = BitmapFactory.decodeResource(resources, resId)

fun View.setTransitionNameCompat(name: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        transitionName = name
    }
}

fun View.getTransitionNameCompat() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) transitionName else ""

fun ImageView.getImageByteArray(): ByteArray {
    val bitmap = (drawable as BitmapDrawable).bitmap
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun Context.drawableByName(name: String): Drawable? {
    val resourceId = resources.getIdentifier(name, "drawable", packageName)
    return ContextCompat.getDrawable(this, resourceId)
}

fun Context.dpToPx(value: Int) = resources.displayMetrics.density * value
fun Context.spToPx(value: Int) = resources.displayMetrics.scaledDensity * value
val Context.screenHeight: Int
    get() = resources.displayMetrics.heightPixels
val Context.screenWidth: Int
    get() = resources.displayMetrics.widthPixels

fun Activity.string(resId: Int) = resources.getString(resId)
fun Context.string(resId: Int) = resources.getString(resId)
fun Fragment.string(resId: Int) = resources.getString(resId)
fun TextView.setTextRes(resId: Int) {
    text = resources.getString(resId)
}

fun Context.color(@ColorRes id: Int) = ContextCompat.getColor(this, id)

inline fun View.onPreDraw(crossinline block: EmptyBlock) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            viewTreeObserver.removeOnPreDrawListener(this)
            block()
            return true
        }
    })
}

inline fun View.onLayout(crossinline block: EmptyBlock) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            block()
        }
    })
}

// EXTENSIONS FOR LOGGING
fun Any.shortLog(expression: String) {
    Log.d("happ", expression)
}

fun <T> T.log(header: String): T {
    Log.e("happy", header)
    Log.i("happy", this.toString())
    return this
}

val String.log: String
    get() {
        Log.d("happ", this)
        return this
    }

val String.info: String
    get() {
        Log.i("happ", this)
        return this
    }

val String.warn: String
    get() {
        Log.w("happ", this)
        return this
    }

val String.error: String
    get() {
        Log.e("happ", this)
        return this
    }

// OTHER EXTENSIONS
inline fun Boolean.ifTrue(body: EmptyBlock): Unit? {
    if (this)
        return body()
    else
        return null
}

inline fun Boolean.ifFalse(body: EmptyBlock): Unit? {
    if (!this)
        return body()
    else
        return null
}

inline fun <T> MutableList<T>.removeBreak(predicate: (T) -> Boolean) {
    forEach {
        if (predicate(it)) {
            remove(it)
            return@forEach
        }
    }
}

fun <T> T?.or(obj: T): T {
    if (isNull())
        return obj
    else
        return this!!
}

typealias EmptyBlock = () -> Unit

fun Any?.isNull(): Boolean = this == null
fun Any?.isNotNull(): Boolean = this != null
inline fun Any?.doIfNotNull(emptyBlock: EmptyBlock) {
    isNotNull().ifTrue(emptyBlock)
}

infix fun <A> A.equal(that: A) = this == that
infix fun <A> A.not(that: A) = this != that

fun <T : Exception> Any.catchIfThrown(block: EmptyBlock, catchBlock: (e: Exception) -> Unit = {}) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        catchBlock(e)
    }
}

inline fun <T : Exception> Any.safeFrom(block: EmptyBlock) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val String.color: Int
    get() = Color.parseColor(this)

class CachedList<out T>(val initializer: () -> List<T>) {
    private var list = initializer()

    fun cached(): List<T> {
        if (list.isEmpty())
            return live()
        else
            return list
    }

    fun live(): List<T> {
        list = initializer()
        return list
    }
}

//REACTIVE EXTENSIONS

fun <T> observableList(onChange: (oldValue: MutableList<T>, newValue: MutableList<T>) -> Unit) =
        Delegates.observable(mutableListOf<T>()) { _, oldValue: MutableList<T>, newValue: MutableList<T> ->
            onChange(oldValue, newValue)
        }

inline fun <T> observableValue(initValue: T, crossinline onChange: (oldValue: T, newValue: T) -> Unit) =
        Delegates.observable(initValue) { _, oldValue: T, newValue: T ->
            onChange(oldValue, newValue)
        }

//COMPONENT EXTENSIONS

val Context.memmoryInfo: ActivityManager.MemoryInfo
    get() = ActivityManager.MemoryInfo().apply {
        activityManager.getMemoryInfo(this)
    }

fun AppCompatActivity.noStatusBar() {
    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Activity.getByteArrayExtra() = intent?.getByteArrayExtra("bytearray")
fun Activity.getStringExtra() = intent?.getStringExtra("string")

fun AppCompatActivity.replaceFragment(container: Int, fragment: Fragment, addToBackStack: Boolean = true, inAnim: Int = 0, outAnim: Int = 0) {
    val a = supportFragmentManager.beginTransaction().setCustomAnimations(inAnim, outAnim)
            .replace(container, fragment)
    addToBackStack.ifTrue {
        a.addToBackStack(null)
    }
    a.commit()
}

fun AppCompatActivity.switchFragments(show: Fragment, hide: Fragment, inAnim: Int = -1, outAnim: Int = -1) {
    supportFragmentManager.beginTransaction().setCustomAnimations(inAnim, outAnim)
            .hide(hide)
            .show(show)
            .commit()
}

inline fun fromApi(version: Int, block: EmptyBlock): Unit? {
    return if (Build.VERSION.SDK_INT >= version)
        block()
    else
        null
}

inline fun toApi(version: Int, block: EmptyBlock) {
    if (Build.VERSION.SDK_INT <= version) block()
}

fun Context.isAccessibilityEnabled(id: String): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val runningServices = am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)
    return runningServices.any { id == it.id }
}

fun ContextWrapper.getAppVersion() = packageManager.getPackageInfo(packageName, 0).versionName

val Context.usageStatsManager: UsageStatsManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    get() = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

//fun Activity.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, text, length).show()
//fun Context.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, text, length).show()
//fun Fragment.toast(text: CharSequence, length: Int = Toast.LENGTH_SHORT) = Toast.makeText(context, text, length).show()

fun Context.hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.hasPermissions(vararg permissions: String): Boolean =
        permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

fun Activity.hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.hasPermissions(vararg permissions: String): Boolean =
        permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

fun Activity.getSharedPrefs(db: String = "db") = getSharedPreferences(db, Context.MODE_PRIVATE)
fun Fragment.getSharedPrefs(db: String = "db") = context?.getSharedPreferences(db, Context.MODE_PRIVATE)
fun Context.getSharedPrefs(db: String = "db") = getSharedPreferences(db, Context.MODE_PRIVATE)

//inline fun<reified T> sharedPrefs(dbName: String = "db"): ReadWriteProperty<Context, T> {
//    return object : ReadWriteProperty<Context,T> {
//        override fun getValue(thisRef: Context, property: KProperty<*>): T {
//            val sp = thisRef.getSharedPreferences(dbName, Context.MODE_PRIVATE)
//            return when (T::class) {
//                Int::class -> sp.getInt(property.name,0) as T
//                Long::class -> sp.getLong(property.name,0) as T
//                Boolean::class -> sp.getBoolean(property.name,false) as T
//                String::class -> sp.getString(property.name,"") as T
//                else -> null as T
//            }
//        }
//
//        override fun setValue(thisRef: Context, property: KProperty<*>, value: T) {
//            thisRef.getSharedPreferences(dbName, Context.MODE_PRIVATE).edit().p
//        }
//    }
//}

fun Activity.shareImage(source: Bitmap, text: String, shareTitle: String) {
    MediaStore.Images.Media.insertImage(contentResolver, source, text, "")?.let {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, shareTitle)
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_STREAM, Uri.parse(it))
            type = "image/*"
            startActivityForResult(Intent.createChooser(this, shareTitle), 56)
        }
    }
}

//IO EXTENSIONS

fun Context.writeFile(name: String, data: ByteArray) {
    FileOutputStream(File("${filesDir.absolutePath}/$name")).apply {
        write(data)
        close()
    }
}

fun writeFile(name: String, data: ByteArray) {
    FileOutputStream(File(name)).apply {
        write(data)
        close()
    }
}

fun Context.fileExists(name: String) = File("${filesDir.absolutePath}/$name").exists()

// COROUTINES EXTENSIONS

suspend fun CoroutineScope.delayMillis(milis: Long) = delay(milis, TimeUnit.MILLISECONDS)

suspend inline fun CoroutineScope.waitWhile(timeFrame: Long = 1000, predicate: () -> Boolean) {
    while (predicate())
        delayMillis(timeFrame)
}

inline fun <T> Deferred<T>.onError(crossinline error: (t: Throwable) -> Unit) {
    invokeOnCompletion { it?.let { it.printStackTrace(); error(it) } }
}

fun <T> Deferred<T>.debug() = apply {
    invokeOnCompletion { it?.let { throw it } }
}

//fun <T> Deferred<T>.catchExceptions() = apply {
//    invokeOnCompletion { it?.let { it.printStackTrace(); Firebase.logException(CoroutineException(it.message.orEmpty())) } }
//}

fun <T> Any.ui(block: suspend CoroutineScope.() -> T): Deferred<T> = async(UI, block = block)

fun <T> Any.bg(block: suspend CoroutineScope.() -> T): Deferred<T> = async(CommonPool, block = block)

fun Any.launchUi(block: suspend CoroutineScope.() -> Unit): Job = launch(UI, block = block)

fun Any.launchBg(block: suspend CoroutineScope.() -> Unit): Job = launch(CommonPool, block = block)

fun Any.interval(millis: Long, block: suspend CoroutineScope.() -> Unit) = ui {
    while (true) {
        block()
        delayMillis(millis)
    }
}

fun CoroutineScope.cancel(cause: Throwable? = null) = context[Job]!!.cancel(cause)

fun Job.cancelOnStop(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.onEvent(onStop = {
        "CANCELING COROUTINE".warn
        cancel()
    })
}

fun Job.cancelOnPause(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.onEvent(onPause = {
        "CANCELING COROUTINE".warn
        cancel()
    })
}

fun LifecycleOwner.onEvent(onCreate: EmptyBlock? = null,
                           onStart: EmptyBlock? = null,
                           onResume: EmptyBlock? = null,
                           onPause: EmptyBlock? = null,
                           onStop: EmptyBlock? = null,
                           onDestroy: EmptyBlock? = null) {
    lifecycle.addObserver(object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            onCreate?.let {
                it.invoke()
                lifecycle.removeObserver(this)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            onStart?.let {
                it.invoke()
                lifecycle.removeObserver(this)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            onResume?.let {
                it.invoke()
                lifecycle.removeObserver(this)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            onPause?.let {
                it.invoke()
                lifecycle.removeObserver(this)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            onStop?.let {
                it.invoke()
                lifecycle.removeObserver(this)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            onDestroy?.let {
                it.invoke()
                lifecycle.removeObserver(this)
            }
        }
    })
}

// TOP LEVEL PROPERTIES

val now: Long
    get() = System.currentTimeMillis()

//val appLifecycle: LifecycleOwner = ProcessLifecycleOwner.get()

//OTHER EXTENSIONS

fun Long.inVicinity(target: Long, range: Long) = this in target - range..target + range

val timezone: Long
    get() = TimeUnit.HOURS.convert(TimeZone.getDefault().rawOffset.toLong(), TimeUnit.MILLISECONDS)