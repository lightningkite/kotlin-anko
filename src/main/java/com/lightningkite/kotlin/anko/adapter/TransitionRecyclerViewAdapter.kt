package com.lightningkite.kotlin.anko.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import java.util.*

/**
 * An adapter for RecyclerViews that contains other adapters.
 *
 * Created by jivie on 5/4/16.
 */
class TransitionRecyclerViewAdapter(
        val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val activeObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            for (index in 0..itemCount - 1) {
                this@TransitionRecyclerViewAdapter.notifyItemMoved(fromPosition + index, toPosition + index)
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            this@TransitionRecyclerViewAdapter.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onChanged() {
            this@TransitionRecyclerViewAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            this@TransitionRecyclerViewAdapter.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            this@TransitionRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            this@TransitionRecyclerViewAdapter.notifyItemRangeChanged(positionStart, itemCount, payload)
        }
    }

    var current: RecyclerView.Adapter<*>? = null

    fun animate(to: RecyclerView.Adapter<*>) {
        val currentSize = current?.itemCount ?: 0
        val newSize = to.itemCount
        val diff = newSize - currentSize
        val min = Math.min(currentSize, newSize)
        current?.unregisterAdapterDataObserver(activeObserver)
        current = to
        if (diff > 0) {
            notifyItemRangeInserted(min, diff)
        } else if (diff < 0) {
            notifyItemRangeRemoved(min, -diff)
        }
        notifyItemRangeChanged(0, min)
        to.registerAdapterDataObserver(activeObserver)

    }

    override fun getItemCount(): Int = current?.itemCount ?: 0

    val types = HashMap<Pair<RecyclerView.Adapter<*>, Int>, Int>()
    val reverseTypes = HashMap<Int, Pair<RecyclerView.Adapter<*>, Int>>()
    override fun getItemViewType(position: Int): Int {
        val pair = current!! to current!!.getItemViewType(position)
        return if (types.containsKey(pair)) {
            types[pair]!!
        } else {
            val new = types.size
            types.put(pair, new)
            reverseTypes.put(new, pair)
            new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = current!!.onCreateViewHolder(parent, viewType)

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
            = (current!! as RecyclerView.Adapter<RecyclerView.ViewHolder>).onBindViewHolder(holder, position)
}

inline fun RecyclerView.transitionAdapter(setup: TransitionRecyclerViewAdapter.() -> Unit) = TransitionRecyclerViewAdapter(context).apply(setup)