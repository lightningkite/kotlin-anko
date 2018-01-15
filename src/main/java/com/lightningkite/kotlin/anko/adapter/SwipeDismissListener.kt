package com.lightningkite.kotlin.anko.adapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Listener for easily making swipe-away gestures for a [RecyclerView].
 * Created by jivie on 2/11/16.
 */
open class SwipeDismissListener(
        val canDismiss: (Int) -> Boolean = { true },
        val action: (Int) -> Unit
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(p0: RecyclerView?, holder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, if (canDismiss(holder.adapterPosition)) ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT else 0)
    }

    override fun onMove(p0: RecyclerView?, p1: RecyclerView.ViewHolder?, p2: RecyclerView.ViewHolder?): Boolean = false


    @Suppress("UNCHECKED_CAST")
    override fun onSwiped(holder: RecyclerView.ViewHolder, swipeDirection: Int) {
        if (swipeDirection == ItemTouchHelper.LEFT || swipeDirection == ItemTouchHelper.RIGHT) {
            action(holder.adapterPosition)
        }
    }
}

/**
 * Easily make swipe-away gestures for a [RecyclerView].
 */
fun RecyclerView.swipeToDismiss(
        canDismiss: (Int) -> Boolean = { true },
        action: (Int) -> Unit
) {
    val listener = SwipeDismissListener(canDismiss, action)
    ItemTouchHelper(listener).attachToRecyclerView(this)
}