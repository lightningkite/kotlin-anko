package com.lightningkite.kotlin.anko.animation

import android.graphics.Color
import com.lightningkite.kotlin.math.degreesTo

/**
 * Created by jivie on 2/9/16.
 */
object Interpolate {


    /**
     * A function that interpolates between colors RGB style.
     */
    val rgb = fun(from: Int, interpolationValue: Float, to: Int): Int {
        val a1 = Color.alpha(from)
        val r1 = Color.red(from)
        val g1 = Color.green(from)
        val b1 = Color.blue(from)

        val a2 = Color.alpha(to)
        val r2 = Color.red(to)
        val g2 = Color.green(to)
        val b2 = Color.blue(to)

        val inv = 1 - interpolationValue

        return Color.argb(
                (a2 * interpolationValue + a1 * inv).toInt(),
                (r2 * interpolationValue + r1 * inv).toInt(),
                (g2 * interpolationValue + g1 * inv).toInt(),
                (b2 * interpolationValue + b1 * inv).toInt()
        )
    }

    /**
     * A function that interpolates between colors HSV style.
     */
    val hsv = fun(from: Int, interpolationValue: Float, to: Int): Int {
        val fromHSV: FloatArray = FloatArray(3)
        Color.colorToHSV(from, fromHSV)
        val toHSV: FloatArray = FloatArray(3)
        Color.colorToHSV(to, toHSV)

        val fromA = Color.alpha(from)
        val toA = Color.alpha(to)

        val diff = fromHSV[0].degreesTo(toHSV[0])

        val inv = 1 - interpolationValue

        val interpHSV: FloatArray = floatArrayOf(
                (fromHSV[0] + diff * interpolationValue + 360) % 360,
                fromHSV[1] * inv + toHSV[1] * interpolationValue,
                fromHSV[2] * inv + toHSV[2] * interpolationValue
        )

        return Color.HSVToColor((toA * interpolationValue + fromA * inv).toInt(), interpHSV)
    }

    /**
     * A function that interpolates between two float values linearly.
     */
    val float = fun(from: Float, interpolationValue: Float, to: Float): Float {
        return from * (1 - interpolationValue) + to * interpolationValue
    }
}