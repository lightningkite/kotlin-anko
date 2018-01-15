package com.lightningkite.kotlin.anko.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContextImpl

/**
 * An adapter with a single view.
 * Created by joseph on 9/20/16.
 */
class SingleRecyclerViewAdapter(
        val makeView: SRVAContext.() -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = 1
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(SRVAContext(this, parent.context).apply { makeView() }.view) {}
    }

    class SRVAContext(adapter: SingleRecyclerViewAdapter, context: Context) : AnkoContextImpl<SingleRecyclerViewAdapter>(context, adapter, false) {
        fun <V : View> V.lparams(
                width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
                init: RecyclerView.LayoutParams.() -> Unit = {}
        ): V {
            val layoutParams = RecyclerView.LayoutParams(width, height)
            layoutParams.init()
            this@lparams.layoutParams = layoutParams

            return this
        }
    }
}

/**
 * Create an adapter that has a single view.
 */
fun RecyclerView.singleAdapter(makeView: SingleRecyclerViewAdapter.SRVAContext.() -> Unit) = SingleRecyclerViewAdapter(makeView)