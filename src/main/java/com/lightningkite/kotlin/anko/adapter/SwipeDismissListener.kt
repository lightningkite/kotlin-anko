package com.lightningkite.kotlin.anko.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

/**
 *
 * Created by jivie on 2/11/16.
 */
open class SwipeDismissListener(
        val canDismiss: (Int) -> Boolean = { true },
        val action: (Int) -> Unit
) : ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        holder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, if (canDismiss(holder.adapterPosition)) ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT else 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false


    @Suppress("UNCHECKED_CAST")
    override fun onSwiped(holder: RecyclerView.ViewHolder, swipeDirection: Int) {
        if (swipeDirection == ItemTouchHelper.LEFT || swipeDirection == ItemTouchHelper.RIGHT) {
            action(holder.adapterPosition)
        }
    }
}

inline fun RecyclerView.swipeToDismiss(
        noinline canDismiss: (Int) -> Boolean = { true },
        noinline action: (Int) -> Unit
) {
    val listener = SwipeDismissListener(canDismiss, action)
    ItemTouchHelper(listener).attachToRecyclerView(this)
}