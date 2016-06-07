package com.ivieleague.kotlin.anko.animation

import android.animation.TimeInterpolator
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
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
    private var duration: Long = 1
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
        if (startValue == null) {
            jump(to)
        } else {
            stop()
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
        shouldRun = false
        startValue = to
        endValue = to
        timeElapsed = 0
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
            if (currentVal == endValue) return
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