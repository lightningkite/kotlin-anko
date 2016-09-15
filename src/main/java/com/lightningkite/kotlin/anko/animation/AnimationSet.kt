package com.lightningkite.kotlin.anko.animation

import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator

/**
 * A set of animation lambdas for transitioning between two views.
 * Created by jivie on 8/6/15.
 */
interface AnimationSet {
    /**
     * An animation lambda for animating the new view in.
     * This - The view being animated.
     * parent - the view group that contains it.
     * @return A [ViewPropertyAnimator] ready to animate the view.  [ViewPropertyAnimator.start] needs to be called on this value to start the animation.
     */
    val animateIn: View.(parent: ViewGroup) -> ViewPropertyAnimator
    /**
     * An animation lambda for animating the old view out.
     * This - The view being animated.
     * parent - the view group that contains it.
     * @return A [ViewPropertyAnimator] ready to animate the view.  [ViewPropertyAnimator.start] needs to be called on this value to start the animation.
     */
    val animateOut: View.(parent: ViewGroup) -> ViewPropertyAnimator

    //operator public fun component1(): View.(ViewGroup) -> ViewPropertyAnimator = animateIn
    //operator public fun component2(): View.(ViewGroup) -> ViewPropertyAnimator = animateOut

    companion object {
        val fade: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                alpha = 0f
                animate().alpha(1f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                alpha = 1f
                animate().alpha(0f).setDuration(300)
            }
        }
        val slidePush: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationX = it.width.toFloat()
                animate().translationX(0f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationX = 0f
                animate().translationX(-it.width.toFloat()).setDuration(300)
            }
        }
        val slidePop: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationX = -it.width.toFloat()
                animate().translationX(0f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationX = 0f
                animate().translationX(it.width.toFloat()).setDuration(300)
            }
        }
        val slideUp: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationY = it.height.toFloat()
                animate().translationY(0f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationY = 0f
                animate().translationY(-it.height.toFloat()).setDuration(300)
            }
        }
        val slideDown: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationY = (-it.height.toFloat())
                animate().translationY(0f).setDuration(300)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                translationY = 0f
                animate().translationY(it.height.toFloat()).setDuration(300)
            }
        }
        val flipVertical: AnimationSet = object : AnimationSet {
            override val animateIn: View.(ViewGroup) -> ViewPropertyAnimator = {
                scaleY = (0f)
                animate().scaleY(1f).setDuration(150).setStartDelay(150)
            }
            override val animateOut: View.(ViewGroup) -> ViewPropertyAnimator = {
                scaleY = (1f)
                animate().scaleY(0f).setDuration(150)
            }
        }
    }
}