package com.lightningkite.kotlin.anko.animation

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import com.lightningkite.kotlin.anko.measureDesiredHeight
import com.lightningkite.kotlin.anko.measureDesiredWidth
import com.lightningkite.kotlin.anko.requestLayoutSafe

/**
 * Various functions to assist with animating things.
 */


fun View.animateHighlight(milliseconds: Long, color: Int, millisecondsTransition: Int = 200) {
    assert(milliseconds > millisecondsTransition * 2) { "The time shown must be at least twice as much as the transition time" }
    val originalBackground = background
    val transition = TransitionDrawable(arrayOf(originalBackground ?: ColorDrawable(0x0), ColorDrawable(color)))
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

fun View.heightAnimator(toHeight: Int = measureDesiredHeight()): TypedValueAnimator.IntAnimator {
    val currentHeight = layoutParams.height
    return TypedValueAnimator.IntAnimator(currentHeight, toHeight).onUpdate {
        layoutParams.height = it
        requestLayoutSafe()
    }
}

fun View.widthAnimator(toWidth: Int = measureDesiredWidth()): TypedValueAnimator.IntAnimator {
    val currentWidth = layoutParams.width
    return TypedValueAnimator.IntAnimator(currentWidth, toWidth).onUpdate {
        layoutParams.width = it
        requestLayoutSafe()
    }
}