package com.lightningkite.kotlin.anko.animation

import android.view.View
import com.lightningkite.kotlin.anko.requestLayoutSafe

/**
 * Various functions to assist with animating things.
 */


fun View.heightAnimator(toHeight: Int): TypedValueAnimator.IntAnimator {
    val currentHeight = layoutParams.height
    return TypedValueAnimator.IntAnimator(currentHeight, toHeight).onUpdate {
        layoutParams.height = it
        requestLayoutSafe()
    }
}

fun View.widthAnimator(toWidth: Int): TypedValueAnimator.IntAnimator {
    val currentWidth = layoutParams.width
    return TypedValueAnimator.IntAnimator(currentWidth, toWidth).onUpdate {
        layoutParams.width = it
        requestLayoutSafe()
    }
}