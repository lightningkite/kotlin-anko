package com.ivieleague.kotlin.anko.animation

import android.animation.TimeInterpolator
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import com.ivieleague.kotlin.math.degreesTo
import com.ivieleague.kotlin.runAll
import java.lang.ref.WeakReference
import java.util.*

/**
 * Animates pretty much anything.
 * @param target The view that is being animated on.
 * @param startValue The initial value that should be shown or null if the first call to animate should just jump.
 * @param action An extension function that changes the view to reflect the value passed in.
 * @param interpolator A function that interpolates between one value and another.
 * Created by jivie on 9/28/15.
 */
class ActionAnimator<T, V>(
        target: T,
        var startValue: V? = null,
        val action: T.(value: V) -> Unit,
        var interpolator: ((startVal: V, progress: Float, endVal: V) -> V),
        var timeInterpolator: TimeInterpolator = defaultInterpolator
) {
    companion object {
        val defaultInterpolator = LinearInterpolator()
    }

    private val weak: WeakReference<T> = WeakReference(target)
    private val handler: Handler = Handler(Looper.getMainLooper())

    private var endValue: V? = null
    private var duration: Long = 0
    private var delta: Long = 16
    private var timeElapsed: Long = 0
    private var shouldRun: Boolean = false

    val onComplete = ArrayList<() -> Unit>()

    /**
     * Animates the property to the new value [to] over [newDuration] milliseconds with [newDelta] milliseconds of precision.
     * @param to The value to animate to.
     * @param newDuration The amount of time to animate over in milliseconds.
     * @param newDelta The time between updates of the animation in milliseconds, defaulted to 20 milliseconds.
     */
    fun animate(
            to: V,
            newDuration: Long,
            newDelta: Long = 16L
    ) {
        stop()
        if (startValue == null) {
            //jump
            weak.get()?.action(to)
            startValue = to
        } else {
            delta = newDelta
            duration = newDuration
            timeElapsed = 0
            endValue = to
            shouldRun = true
            handler.postDelayed(runnable, delta)
        }
    }

    fun jump(
            to: V
    ) {
        stop()
        startValue = to
        weak.get()?.action(to)
        onComplete.runAll()
    }

    /**
     * Immediately cancels the current animation.
     */
    fun stop() {
        if (endValue != null && startValue != null) {
            startValue = interpolator(startValue!!, timeElapsed.toFloat() / duration, endValue!!)
        }
        shouldRun = false
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (!shouldRun || endValue == null || startValue == null) return

            timeElapsed = Math.min(timeElapsed + delta, duration)
            val t = timeInterpolator.getInterpolation(timeElapsed.toFloat() / duration)
            val currentVal = interpolator(startValue!!, t, endValue!!)
            weak.get()?.action(currentVal)

            if (timeElapsed < duration && weak.get() != null) {
                handler.postDelayed(this, delta)
            } else {
                startValue = endValue!!
                weak.get()?.action(endValue!!)
                onComplete.runAll()
            }
        }
    }
}

/**
 * A function that interpolates between colors RGB style.
 */
fun interpolateRGB(from: Int, interpolationValue: Float, to: Int): Int {
    val a1 = Color.alpha(from)
    val r1 = Color.red(from)
    val g1 = Color.green(from)
    val b1 = Color.blue(from)

    val a2 = Color.alpha(to)
    val r2 = Color.red(to)
    val g2 = Color.green(to)
    val b2 = Color.blue(to)

    val inv = 1 - interpolationValue

    return Color.argb(
            (a2 * interpolationValue + a1 * inv).toInt(),
            (r2 * interpolationValue + r1 * inv).toInt(),
            (g2 * interpolationValue + g1 * inv).toInt(),
            (b2 * interpolationValue + b1 * inv).toInt()
    )
}

/**
 * A function that interpolates between colors HSV style.
 */
fun interpolateHSV(from: Int, interpolationValue: Float, to: Int): Int {
    val fromHSV: FloatArray = FloatArray(3)
    Color.colorToHSV(from, fromHSV)
    val toHSV: FloatArray = FloatArray(3)
    Color.colorToHSV(to, toHSV)

    val fromA = Color.alpha(from)
    val toA = Color.alpha(to)

    val diff = fromHSV[0].degreesTo(toHSV[0])

    val inv = 1 - interpolationValue

    val interpHSV: FloatArray = floatArrayOf(
            (fromHSV[0] + diff * interpolationValue + 360) % 360,
            fromHSV[1] * inv + toHSV[1] * interpolationValue,
            fromHSV[2] * inv + toHSV[2] * interpolationValue
    )

    return Color.HSVToColor((toA * interpolationValue + fromA * inv).toInt(), interpHSV)
}

/**
 * A function that interpolates between two float values linearly.
 */
fun interpolate(from: Float, interpolationValue: Float, to: Float): Float {
    return from * (1 - interpolationValue) + to * interpolationValue
}