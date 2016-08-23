package com.lightningkite.kotlin.anko.animation;

import android.animation.TimeInterpolator
import android.view.View
import android.view.ViewGroup

class ViewHeightAnimator(val target: View, val minHeight: Int = 0, val maxHeight: Int = Int.MAX_VALUE, val duration: Long = 50, val timeInterpolator: TimeInterpolator = ActionAnimator.defaultInterpolator) {

    fun toggle() {
        if(target.height == minHeight) {
            expand()
        } else {
            contract()
        }
    }

    fun expand() {
        val endValue = getHeight()
        ActionAnimator(
                target,
                minHeight.toFloat(),
                { updatedValue -> layoutParams.height = updatedValue.toInt(); target.requestLayout() },
                Interpolate.float,
                timeInterpolator
        ).animate(endValue.toFloat(), duration)
    }

    fun contract() {
        val startValue = getHeight()
        ActionAnimator(
                target,
                startValue.toFloat(),
                { updatedValue -> layoutParams.height = updatedValue.toInt(); target.requestLayout() },
                Interpolate.float,
                timeInterpolator
        ).animate(minHeight.toFloat(), duration)
    }

    private fun getHeight(): Int {
        if(maxHeight == Int.MAX_VALUE) {
            target.measure(View.MeasureSpec.makeMeasureSpec((target.parent as ViewGroup).width, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            return target.measuredHeight
        } else {
            return maxHeight
        }
    }
}