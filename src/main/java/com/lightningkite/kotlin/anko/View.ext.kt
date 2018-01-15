package com.lightningkite.kotlin.anko

import android.app.Activity
import android.graphics.Point
import android.os.Build
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.inputMethodManager

/**
 * Convenience function for `ViewCompat.isAttachedToWindow(this)`.
 */
inline fun View.isAttachedToWindowCompat(): Boolean = ViewCompat.isAttachedToWindow(this)

/**
 * Allows you to modify the elevation on a view without worrying about version.
 */
var View.elevationCompat: Float
    get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return elevation
        }
        return 0f
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = value
        }
    }

/**
 * Gets the top of the view visually.
 */
val View.visualTop: Int get() {
    val layoutParams = layoutParams
    return top - (
            (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
            ) + (translationY + .5f).toInt()
}

/**
 * Gets the bottom of the view visually.
 */
val View.visualBottom: Int get() {
    val layoutParams = layoutParams
    return bottom + (
            (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
            ) + (translationY + .5f).toInt()
}


/**
 * Attempts to get the activity of the view.
 */
fun View.getActivity(): Activity? {
    return context.getActivity()
}

/**
 * Shows the soft input for the vindow.
 */
fun View.showSoftInput() {
    context.inputMethodManager.showSoftInput(this, 0)
}

/**
 * Hides the soft input for the vindow.
 */
fun View.hideSoftInput() {
    context.inputMethodManager.hideSoftInputFromWindow(this.applicationWindowToken, 0)
}

/**
 * Measures the desired size of the view.
 */
fun View.measureDesiredSize(maxWidth: Int = Int.MAX_VALUE, maxHeight: Int = Int.MAX_VALUE): Point {
    this.measure(
            View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST)
    )
    return Point(measuredWidth, measuredHeight)
}

/**
 * Checks if the view is in layout, returns false if SDK version isn't high enough.
 */
fun View.isInLayoutCompat(): Boolean {
    return if (Build.VERSION.SDK_INT >= 18)
        isInLayout
    else
        false
}

/**
 * Requests a layout in a more safe manner by checking if we are currently in a layout pass.
 */
fun View.requestLayoutSafe() {
    if (Build.VERSION.SDK_INT >= 18) {
        if (!isInLayout) {
            requestLayout()
        }
    } else {
        requestLayout()
    }
}

/**
 * Sets an on click listener for a view, but ensures the action cannot be triggered more often than [cooldown] milliseconds.
 */
inline fun View.onClickWithCooldown(cooldown: Long = 1000L, crossinline action: () -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        var lastTime = 0L
        override fun onClick(v: View?) {
            val now = System.currentTimeMillis()
            if (now - lastTime > cooldown) {
                action()
                lastTime = now
            }
        }
    })
}


