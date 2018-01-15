package com.lightningkite.kotlin.anko

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat

/**
 * Compat wrappers
 * Created by jivie on 5/4/16.
 */

fun Resources.getColorCompat(resource: Int): Int = ResourcesCompat.getColor(this, resource, null)

fun Resources.getDrawableCompat(resource: Int): Drawable = ResourcesCompat.getDrawable(this, resource, null)!!

fun Resources.getColorStateListCompat(resource: Int): ColorStateList = ResourcesCompat.getColorStateList(this, resource, null)!!