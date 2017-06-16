package com.lightningkite.kotlin.anko

import android.app.Activity
import android.graphics.Point
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
import java.util.*

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
            (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0
            ) + (translationY + .5f).toInt()
};

val View.visualBottom: Int get() {
    val layoutParams = layoutParams
    return bottom + (
            (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
            ) + (translationY + .5f).toInt()
};


fun View.getActivity(): Activity? {
    return context.getActivity()
}


fun View.showSoftInput() {
    context.inputMethodManager.showSoftInput(this, 0)
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

inline fun View.measureDesiredSize(maxWidth: Int = Int.MAX_VALUE, maxHeight: Int = Int.MAX_VALUE): Point {
    this.measure(
            View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST)
    )
    return Point(measuredWidth, measuredHeight)
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


//View lifecycle stuff

private val View_lifecycleListener = WeakHashMap<View, ViewLifecycleListener>()

class ViewLifecycleListener(val view: View) : View.OnAttachStateChangeListener, LifecycleConnectable {

    var attached = view.isAttachedToWindowCompat()
        private set
    private val lifecycleListeners = ArrayList<LifecycleListener>()

    override fun onViewDetachedFromWindow(v: View?) {
        if (!attached) {
            println("Broken cycling detected in onViewDetachedFromWindow $view")
            return
        }
        lifecycleListeners.forEach { it.onStop() }
        attached = false
    }

    override fun onViewAttachedToWindow(v: View?) {
        if (attached) {
            println("Broken cycling detected in onViewAttachedToWindow $view")
            return
        }
        lifecycleListeners.forEach { it.onStart() }
        attached = true
    }

    override fun connect(listener: LifecycleListener) {
        if (attached) {
            listener.onStart()
        }
        lifecycleListeners.add(listener)
    }

    fun setAlwaysOn() {
        view.removeOnAttachStateChangeListener(this)
        if (!attached) {
            attached = true
            lifecycleListeners.forEach { it.onStart() }
        }
    }

    fun setAlwaysOnRecursive() {
        setAlwaysOn()
        view.forThisAndAllChildrenRecursive {
            View_lifecycleListener[it]?.setAlwaysOn()
        }
    }
}

fun View.forThisAndAllChildrenRecursive(action: (View) -> Unit) {
    action.invoke(this)
    if (this is ViewGroup) {
        for (i in 0..this.childCount - 1) {
            getChildAt(i).forThisAndAllChildrenRecursive(action)
        }
    }
}

/**
 * Gets a lifecycle object for events to connect with.
 * There is only one lifecycle object that is recycled, so the lifecycle returned expires when
 * another lifecycle is requested.
 */
val View.lifecycle: ViewLifecycleListener
    get() = View_lifecycleListener.getOrPut(this) {
        val listener = ViewLifecycleListener(this)
        addOnAttachStateChangeListener(listener)
        listener
    }
