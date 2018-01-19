package com.lightningkite.kotlin.anko.animation

import android.content.Context
import android.view.View
import android.view.ViewManager
import android.widget.FrameLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.matchParent

/**
 * A view that swaps between views that are passed in.
 *
 * Created by joseph on 1/19/18.
 */
class SwapView(context: Context) : FrameLayout(context) {
    val manager = ViewSwapManager(this, { FrameLayout.LayoutParams(matchParent, matchParent) })
    fun swap(newView: View, animation: AnimationSet = AnimationSet.fade) = manager.swap(newView, animation)
}

fun ViewManager.swapView(init: SwapView.() -> Unit) = ankoView({ SwapView(it) }, 0, init)