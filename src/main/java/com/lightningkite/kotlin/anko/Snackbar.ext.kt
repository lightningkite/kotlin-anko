package com.lightningkite.kotlin.anko

import android.content.Context
import android.graphics.Color
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import org.jetbrains.anko.findOptional

/**
 * Created by josep on 3/3/2016.
 */

private fun View.setSubviewsTextColor(color: Int) {
    if (this is ViewGroup) {
        for (subviewIndex in 0..childCount) {
            val subview = getChildAt(subviewIndex)
            subview.setSubviewsTextColor(color)
        }
    }
    if (this is TextView) {
        this.setTextColor(color)
    }
}

private fun ForcePrivateSnackbarConstructor(group: ViewGroup): Snackbar {
//    val constructor = Snackbar::class.java.getDeclaredConstructor(ViewGroup::class.java)
//    constructor.isAccessible = true
//    return constructor.newInstance(group)
    return Snackbar.make(group, "", Snackbar.LENGTH_SHORT)
}

fun View.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    try {
        val snack = ForcePrivateSnackbarConstructor(findSuitableParent(this))
        snack.setText(text)
        snack.duration = duration
        snack.view.setSubviewsTextColor(Color.WHITE)
        snack.init()
        snack.show()
    } catch(e: Exception) {
        e.printStackTrace()
    }
}

fun View.snackbar(text: Int, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    try {
        val snack = ForcePrivateSnackbarConstructor(findSuitableParent(this))
        snack.setText(text)
        snack.duration = duration
        snack.view.setSubviewsTextColor(Color.WHITE)
        snack.init()
        snack.show()
    } catch(e: Exception) {
        e.printStackTrace()
    }
}

fun Snackbar.onDismissed(lambda: (event: Int) -> Unit) {
    setCallback(object : Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar?, event: Int) {
            lambda(event)
        }
    })
}

fun Context.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    var view = getActivity()?.findOptional<View>(android.R.id.content)
    view = when (view) {
        is ScrollView -> view.getChildAt(0)
        is NestedScrollView -> view.getChildAt(0)
        else -> view
    }
    try {
        view?.snackbar(text, duration, init)
    } catch(e: Exception) {
        e.printStackTrace()
    }
}

fun Context.snackbar(text: Int, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    var view = getActivity()?.findOptional<View>(android.R.id.content)
    view = when (view) {
        is ScrollView -> view.getChildAt(0)
        is NestedScrollView -> view.getChildAt(0)
        else -> view
    }
    try {
        view?.snackbar(text, duration, init)
    } catch(e: Exception) {
        e.printStackTrace()
    }
}

fun android.support.design.widget.Snackbar.callback(init: _Snackbar_Callback.() -> Unit) {
    val callback = _Snackbar_Callback()
    callback.init()
    setCallback(callback)
}

class _Snackbar_Callback : android.support.design.widget.Snackbar.Callback() {

    private var _onShown: ((Snackbar?) -> Unit)? = null
    private var _onDismissed: ((Snackbar?, Int) -> Unit)? = null

    override fun onShown(snackbar: Snackbar?) {
        _onShown?.invoke(snackbar)
    }

    fun onSnackbarShown(listener: (Snackbar?) -> Unit) {
        _onShown = listener
    }

    override fun onDismissed(snackbar: Snackbar?, event: Int) {
        _onDismissed?.invoke(snackbar, event)
    }

    fun onSnackbarDismissed(listener: (Snackbar?, Int) -> Unit) {
        _onDismissed = listener
    }
}

private fun findSuitableParent(startView: View?): ViewGroup {
    var view = startView
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            // We've found a CoordinatorLayout, use it
            return view
        } else if (view is FrameLayout && view !is NestedScrollView) {
            if (view.id == android.R.id.content) {
                // If we've hit the decor content view, then we didn't find a CoL in the
                // hierarchy, so use it.
                return view
            } else {
                // It's not the content view but we'll use it as our fallback
                fallback = view as ViewGroup?
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback ?: throw IllegalStateException()
}