package com.lightningkite.kotlin.anko.animation

import android.animation.TimeInterpolator
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.wrapContent

/**
 * Various functions to assist with animating things.
 */


/**
 * Creates a resizer function.
 */
fun View.makeHeightAnimator(
        duration: Long,
        startSize: Float? = null,
        popFix: Boolean = false,
        timeInterpolator: TimeInterpolator = ActionAnimator.defaultInterpolator
): (toSize: Float?) -> Unit {

    var nextGoal: Float? = null

    val heightAnimator = ActionAnimator(this, startSize, {
        if (popFix && nextGoal == it) {
            layoutParams.height = wrapContent
            requestLayout()
        } else {
            layoutParams.height = it.toInt()
            requestLayout()
        }
    }, Interpolate.float, timeInterpolator)

    return { toSize: Float? ->
        nextGoal = null
        if (toSize == null) {
            this.measure(
                    View.MeasureSpec.makeMeasureSpec((parent as ViewGroup).width, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            nextGoal = measuredHeight.toFloat()
        }
        val newHeight = toSize ?: measuredHeight.toFloat()
        heightAnimator.animate(newHeight, duration)
    }
}

/**
 * Creates a resizer function.
 */
fun View.makeWidthAnimator(
        duration: Long,
        startSize: Float? = null,
        onComplete: () -> Unit = {},
        timeInterpolator: TimeInterpolator = ActionAnimator.defaultInterpolator
): (toSize: Float?) -> Unit {

    val widthAnimator = ActionAnimator(this, startSize, {
        layoutParams.width = it.toInt()
        requestLayout()
    }, Interpolate.float, timeInterpolator)
    widthAnimator.onComplete.add(onComplete)

    return { toSize: Float? ->
        if (toSize == null) {
            this.measure(
                    View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED
            )
        }
        val newWidth = toSize ?: measuredWidth.toFloat()
        widthAnimator.animate(newWidth, duration)
    }
}

fun View.animateHighlight(milliseconds: Long, color: Int, millisecondsTransition: Int = 200) {
    assert(milliseconds > millisecondsTransition * 2) { "The time shown must be at least twice as much as the transition time" }
    val originalBackground = background
    val transition = TransitionDrawable(arrayOf(originalBackground, ColorDrawable(color)))
    transition.isCrossFadeEnabled = false
    background = transition
    transition.startTransition(millisecondsTransition)
    postDelayed({
        transition.reverseTransition(millisecondsTransition)
        postDelayed({
            background = originalBackground
        }, millisecondsTransition.toLong())
    }, milliseconds - millisecondsTransition)
}


//DEPRECATED


/**
 * Creates a resizer function.
 */
@Deprecated("Use makeHeightAnimator instead.")
fun View.animateHeightUpdate(duration: Long, startSize: Float? = null): () -> Unit {

    val heightAnimator = ActionAnimator(this, startSize, {
        layoutParams.height = it.toInt()
        requestLayout()
    }, Interpolate.float)

    return {
        this.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
        )
        val newHeight = measuredHeight
        heightAnimator.animate(newHeight.toFloat(), duration)
    }
}

/**
 * Creates a resizer function.
 */
@Deprecated("Use makeWidthAnimator instead.")
fun View.animateWidthUpdate(duration: Long, startSize: Float? = null): () -> Unit {

    val widthAnimator = ActionAnimator(this, startSize, {
        layoutParams.width = it.toInt()
        requestLayout()
    }, Interpolate.float)

    return {
        this.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
        )
        val newWidth = measuredWidth
        widthAnimator.animate(newWidth.toFloat(), duration)
    }
}