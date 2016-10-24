package com.lightningkite.kotlin.anko

import android.app.Activity
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lightningkite.kotlin.lifecycle.LifecycleConnectable
import com.lightningkite.kotlin.lifecycle.LifecycleListener
import org.jetbrains.anko.inputMethodManager

/**
 * Created by jivie on 3/28/16.
 */
inline fun View.isAttachedToWindowCompat(): Boolean = ViewCompat.isAttachedToWindow(this)

inline fun View.setBackground(vararg list: Drawable) {
    background = LayerDrawable(list)
}

inline fun View.setBackground(vararg list: Int) {
    background = LayerDrawable(Array(list.size) { resources.getDrawable(list[it]) })
}

inline fun <T : View> T.lparamsMod(width: Int, height: Int): T {
    layoutParams.apply {
        this.width = width
        this.height = height
    }
    return this
}

inline fun <T : View> T.lparamsMod(width: Int, height: Int, weight: Float): T {
    layoutParams.apply {
        if (this !is LinearLayout.LayoutParams) throw IllegalArgumentException("This only works on LinearLayout.LayoutParams")
        this.width = width
        this.height = height
        this.weight = weight
    }
    return this
}

inline fun <T : View> T.lparamsMod(width: Int, height: Int, lambda: ViewGroup.MarginLayoutParams.() -> Unit): T {
    layoutParams.apply {
        if (this !is ViewGroup.MarginLayoutParams) throw IllegalArgumentException("This only works on ViewGroup.MarginLayoutParams")
        this.width = width
        this.height = height
        lambda()
    }
    return this
}

inline fun <T : View> T.lparamsMod(lambda: ViewGroup.MarginLayoutParams.() -> Unit): T {
    layoutParams.apply {
        if (this !is ViewGroup.MarginLayoutParams) throw IllegalArgumentException("This only works on ViewGroup.MarginLayoutParams")
        lambda()
    }
    return this
}

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

val View.visualTop: Int get() {
    val layoutParams = layoutParams
    return top - (
            if (layoutParams is ViewGroup.MarginLayoutParams)
                layoutParams.topMargin
            else
                0
            ) + (translationY + .5f).toInt()
};

val View.visualBottom: Int get() {
    val layoutParams = layoutParams
    return bottom + (
            if (layoutParams is ViewGroup.MarginLayoutParams)
                layoutParams.bottomMargin
            else
                0
            ) + (translationY + .5f).toInt()
};


fun View.getActivity(): Activity? {
    return context.getActivity()
}


fun View.hideSoftInput() {
    context.inputMethodManager.hideSoftInputFromWindow(this.applicationWindowToken, 0)
}

inline fun View.measureDesiredWidth(): Int {
    this.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec((parent as ViewGroup).height, View.MeasureSpec.AT_MOST)
    )
    return measuredHeight
}

inline fun View.measureDesiredHeight(): Int {
    this.measure(
            View.MeasureSpec.makeMeasureSpec((parent as ViewGroup).width, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    return measuredHeight
}

inline fun View.isInLayoutCompat(): Boolean {
    return if (Build.VERSION.SDK_INT >= 18)
        isInLayout
    else
        false
}

inline fun View.requestLayoutSafe() {
    if (Build.VERSION.SDK_INT >= 18) {
        if (!isInLayout) {
            requestLayout()
        }
    } else {
        requestLayout()
    }
}

/**
 * Various extension functions to support bonds.
 * Created by jivie on 7/22/15.
 */

object ViewLifecycleConnecter : LifecycleConnectable {

    var view: View? = null

    override fun connect(listener: LifecycleListener) {
        if (view?.isAttachedToWindowCompat() ?: false) {
            listener.onStart()
        }
        view?.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                listener.onStart()
            }

            override fun onViewDetachedFromWindow(v: View?) {
                listener.onStop()
            }
        })
    }
}


/**
 * Gets a lifecycle object for events to connect with.
 * There is only one lifecycle object that is recycled, so the lifecycle returned expires when
 * another lifecycle is requested.
 */
val View.lifecycle: LifecycleConnectable
    get() {
        ViewLifecycleConnecter.view = this
        return ViewLifecycleConnecter
    }


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