package com.lightningkite.kotlin.anko.adapter

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup

/**
 * A simple adapter for [ViewPager].
 * Created by joseph on 11/16/16.
 */
open class BasicViewPagerAdapter(val makeView: ViewGroup.(Int) -> View) : PagerAdapter() {

    var itemCount: Int = 0

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return makeView(container, position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, item: Any) {
        container.removeView(item as View)
    }

    override fun getCount(): Int = itemCount

    override fun isViewFromObject(view: View, item: Any): Boolean {
        return view == item
    }
}

/**
 * A simple adapter for [ViewPager], where views are built by [makeView].
 */
fun ViewPager.basicAdapter(count: Int, makeView: ViewGroup.(Int) -> View) = BasicViewPagerAdapter(makeView).apply {
    itemCount = count
}