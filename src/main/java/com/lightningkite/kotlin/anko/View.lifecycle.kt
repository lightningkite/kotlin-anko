package com.lightningkite.kotlin.anko

import android.view.View
import android.view.ViewGroup
import com.lightningkite.kotlin.lifecycle.LifecycleConnectable
import com.lightningkite.kotlin.lifecycle.LifecycleListener
import java.util.*

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
 */
val View.lifecycle: ViewLifecycleListener
    get() = View_lifecycleListener.getOrPut(this) {
        val listener = ViewLifecycleListener(this)
        addOnAttachStateChangeListener(listener)
        listener
    }