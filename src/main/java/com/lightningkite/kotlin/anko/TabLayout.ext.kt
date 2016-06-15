package com.lightningkite.kotlin.anko

import android.support.design.widget.TabLayout
import org.jetbrains.anko.sp

/**
 * Various functions that are missing from TabLayout to use it programmatically.
 *
 * Created by jivie on 4/28/16.
 */

inline fun TabLayout.setTabTextSize(sp: Float) {
    val field = TabLayout::class.java.getDeclaredField("mTabTextSize")
    field.isAccessible = true
    field.set(this, sp(sp))
}

inline fun TabLayout.setTabBackgroundResource(res: Int) {
    val field = TabLayout::class.java.getDeclaredField("mTabBackgroundResId")
    field.isAccessible = true
    field.set(this, res)
}