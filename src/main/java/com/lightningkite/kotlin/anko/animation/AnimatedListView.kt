package com.lightningkite.kotlin.anko.animation

import android.content.Context
import android.view.View
import android.view.ViewManager
import android.widget.AdapterView
import android.widget.ListAdapter
import android.widget.ListView
import org.jetbrains.anko._FrameLayout
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.matchParent

/**
 * A [ListView]-like view that animates between different adapters
 * Created by jivie on 8/7/15.
 */
class AnimatedListView(context: Context) : _FrameLayout(context) {
    private var front: ListView = ListView(context)
    private var back: ListView = ListView(context)

    private var _onItemClick: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit = { a, b, c, d -> }
    var onItemClick: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
        get() = _onItemClick
        set(value) {
            _onItemClick = value
            front.setOnItemClickListener(value)
        }
    private var _onItemLongClick: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Boolean = { a, b, c, d -> true }
    var onItemLongClick: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Boolean
        get() = _onItemLongClick
        set(value) {
            _onItemLongClick = value
            front.setOnItemLongClickListener(value)
        }

    fun onItemClick(func: (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit) {
        onItemClick = func
    }

    init {
        back.lparams(matchParent, matchParent)
        back.visibility = View.GONE
        front.lparams(matchParent, matchParent)
        addView(back)
        addView(front)
    }

    var adapter: ListAdapter?
        get() = front.adapter
        set(value) = setAdapter(value)

    fun setAdapter(adapter: ListAdapter?, animationSet: AnimationSet = AnimationSet.fade) {
        val temp = front
        front = back
        back = temp

        back.onItemClickListener = null
        front.setOnItemClickListener(_onItemClick)

        back.onItemLongClickListener = null
        front.setOnItemLongClickListener(_onItemLongClick)

        back.(animationSet.animateOut)(this).withEndAction {
            back.visibility = View.GONE
        }.start()

        front.adapter = adapter
        front.visibility = View.VISIBLE
        front.(animationSet.animateIn)(this).start()
    }
}

@Suppress("NOTHING_TO_INLINE") inline fun ViewManager.animatedListView() = animatedListView {}
inline fun ViewManager.animatedListView(init: AnimatedListView.() -> Unit): AnimatedListView {
    return ankoView({ AnimatedListView(it) }, init)
}