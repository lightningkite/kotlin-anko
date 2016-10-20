package com.lightningkite.kotlin.anko

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build

/**
 * Created by jivie on 5/4/16.
 */
fun Resources.getColorCompat(resources: Int): Int {
    return if (Build.VERSION.SDK_INT >= 23)
        getColor(resources, null)
    else
        getColor(resources)
}

fun Resources.getDrawableCompat(resources: Int): Drawable {
    return if (Build.VERSION.SDK_INT >= 23)
        getDrawable(resources, null)
    else
        getDrawable(resources)
}