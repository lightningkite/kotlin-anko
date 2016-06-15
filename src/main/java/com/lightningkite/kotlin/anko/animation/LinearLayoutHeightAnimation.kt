package com.lightningkite.kotlin.anko.animation

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout

/**
 * An animation which animates the height of a view inside a vertical linear layout.
 * Created by Joseph on 2/25/2015.
 */
class LinearLayoutHeightAnimation(private val mView: View?, duration: Int, private val mStartHeight: Int, private val mEndHeight: Int) : Animation() {
    private val mLayoutParams: LinearLayout.LayoutParams

    init {
        setDuration(duration.toLong())
        if (mStartHeight < 0)
            throw IllegalArgumentException("startHeight must be positive or zero.")
        if (mEndHeight < 0)
            throw IllegalArgumentException("endHeight must be positive or zero.")
        if (duration < 0)
            throw IllegalArgumentException("duration must be a positive number.")
        if (mView == null)
            throw IllegalArgumentException("view must not be null.")
        if (mView.layoutParams !is LinearLayout.LayoutParams)
            throw IllegalArgumentException("view must be in a linear layout with linear layout parameters.")
        mLayoutParams = mView.layoutParams as LinearLayout.LayoutParams
        mLayoutParams.height = mStartHeight
        mView.visibility = View.VISIBLE
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        if (interpolatedTime < 1.0f) {
            mLayoutParams.height = interpolate(interpolatedTime)
            mView!!.requestLayout()
        } else {
            if (mEndHeight == 0)
                mView!!.visibility = View.GONE
        }
    }

    protected fun interpolate(interpolatedTime: Float): Int {
        val amount = (1 - Math.cos(interpolatedTime * Math.PI)).toFloat() / 2f //sin
        //float amount = interpolatedTime //linear
        return (mStartHeight * (1 - amount) + mEndHeight * amount).toInt()
    }
}