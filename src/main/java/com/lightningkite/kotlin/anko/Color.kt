package com.lightningkite.kotlin.anko

import android.graphics.Color

/**
 * Various functions for manipulating colors.
 * Created by jivie on 5/23/16.
 */

fun Int.alpha(alpha: Int): Int {
    return (this and 0x00FFFFFF) or (alpha shl 24)
}

fun Int.alpha(alpha: Float): Int {
    return (this and 0x00FFFFFF) or ((alpha.coerceIn(0f, 1f) * 0xFF).toInt() shl 24)
}

fun Int.colorMultiply(value: Double): Int {
    return Color.argb(
            Color.alpha(this),
            (Color.red(this) * value).toInt().coerceIn(0, 255),
            (Color.green(this) * value).toInt().coerceIn(0, 255),
            (Color.blue(this) * value).toInt().coerceIn(0, 255)
    )
}

fun Int.colorMultiply(value: Float): Int {
    return Color.argb(
            Color.alpha(this),
            (Color.red(this) * value).toInt().coerceIn(0, 255),
            (Color.green(this) * value).toInt().coerceIn(0, 255),
            (Color.blue(this) * value).toInt().coerceIn(0, 255)
    )
}

fun Int.colorAdd(value: Double): Int {
    return Color.argb(
            Color.alpha(this),
            (Color.red(this) + (value * 0xFF).toInt()).coerceIn(0, 255),
            (Color.green(this) + (value * 0xFF).toInt()).coerceIn(0, 255),
            (Color.blue(this) + (value * 0xFF).toInt()).coerceIn(0, 255)
    )
}

fun Int.colorAdd(value: Float): Int {
    return Color.argb(
            Color.alpha(this),
            (Color.red(this) + (value * 0xFF).toInt()).coerceIn(0, 255),
            (Color.green(this) + (value * 0xFF).toInt()).coerceIn(0, 255),
            (Color.blue(this) + (value * 0xFF).toInt()).coerceIn(0, 255)
    )
}