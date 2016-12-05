package com.lightningkite.kotlin.anko

import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.widget.NestedScrollView
import android.view.View
import android.view.ViewGroup
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

fun View.snackbar(text: CharSequence, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    try {
        val snack = Snackbar.make(this, text, duration)
        snack.view.setSubviewsTextColor(Color.WHITE)
        snack.init()
        snack.show()
    } catch(e: Exception) {
        e.printStackTrace()
    }
}

fun View.snackbar(text: Int, duration: Int = Snackbar.LENGTH_LONG, init: Snackbar.() -> Unit = {}) {
    try {
        val snack = Snackbar.make(this, text, duration)
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
