package com.lightningkite.kotlin.anko.animation

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko.custom.ankoView
import java.util.*

/**
 * A container view that transitions between the child views.
 * Use this class Anko-style, and append the views with the [tag] function.
 * Created by jivie on 8/26/15.
 */
class TransitionView(context: Context) : _FrameLayout(context) {
    private val views: HashMap<String, View> = HashMap()
    private var currentView: View = View(context)
    var defaultAnimation: AnimationSet = AnimationSet.fade

    fun addView(tag: String, child: View) {
        super.addView(child)
        views.put(tag, child)
        child.visibility = View.INVISIBLE
    }

    fun removeView(tag: String) {
        super.removeView(views.remove(tag))
    }

    override fun generateDefaultLayoutParams(): LayoutParams? {
        val params = super.generateDefaultLayoutParams()
        params.gravity = Gravity.CENTER
        return params
    }

    /**
     * Tags a view with [myTag].
     * @param myTag The tag used to access this view later.
     */
    fun <T : View> T.tag(myTag: String): T {
        views.put(myTag, this)
        visibility = View.INVISIBLE
        return this
    }

    /**
     * Animates using [set] to the view designated by [tag].
     * @param tag The view to animate to.  You can tag views using [tag].
     * @param set The animation set for animating.
     */
    fun animate(tag: String, set: AnimationSet = defaultAnimation) = animate(views[tag] ?: throw IllegalArgumentException(), set)

    inline fun animate(index: Int, set: AnimationSet = defaultAnimation) = animate(getChildAt(index), set)

    /**
     * Animates using [set] to the given view.
     * @param view The view to animate to.
     * @param set The animation set for animating.
     */
    fun animate(newView: View, set: AnimationSet = defaultAnimation) {
        if (newView == currentView) return;
        val animateIn = set.animateIn
        val animateOut = set.animateOut
        val oldView = currentView

        newView.visibility = View.VISIBLE
        newView.animateIn(this).start()
        oldView.animateOut(this).withEndAction {
            oldView.visibility = View.INVISIBLE
        }.start()

        currentView = newView
    }

    fun jump(view: View) {
        currentView.visibility = View.INVISIBLE
        currentView = view
        currentView.visibility = View.VISIBLE
    }

    fun jump(tag: String) = jump(views[tag] ?: throw IllegalArgumentException())
    inline fun jump(index: Int) = jump(getChildAt(index))
}

@Suppress("NOTHING_TO_INLINE") inline fun ViewManager.transitionView() = transitionView {}

inline fun ViewManager.transitionView(init: TransitionView.() -> Unit): TransitionView {
    return ankoView({ TransitionView(it) }, init)
}