package com.lightningkite.kotlin.anko

import com.google.android.material.tabs.TabLayout
import org.jetbrains.anko.sp

/**
 * Various functions that are missing from TabLayout to use it programmatically.
 *
 * Created by jivie on 4/28/16.
 */

inline fun TabLayout.setTabTextSize(sp: Float) {
    val field = TabLayout::class.java.getDeclaredField("tabTextSize")
    field.isAccessible = true
    field.set(this, sp(sp))
}

inline fun TabLayout.setTabBackgroundResource(res: Int) {
    val field = TabLayout::class.java.getDeclaredField("tabBackgroundResId")
    field.isAccessible = true
    field.set(this, res)
}