package com.lightningkite.kotlin.anko

import android.graphics.PorterDuff
import android.widget.EditText
import android.widget.TextView

/**
 * Extensions functions on EditText.
 * Created by jivie on 5/2/16.
 */

fun EditText.resetCursorColor() {
    try {
        val f = TextView::class.java.getDeclaredField("mCursorDrawableRes");
        f.isAccessible = true;
        f.set(this, 0);
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}

fun EditText.setCursorColor(color: Int) {
    try {
        val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes");
        fCursorDrawableRes.setAccessible(true);
        val mCursorDrawableRes = fCursorDrawableRes.getInt(this);
        val fEditor = TextView::class.java.getDeclaredField("mEditor");
        fEditor.isAccessible = true;
        val editor = fEditor.get(this);
        val clazz = editor.javaClass;
        val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
        fCursorDrawable.isAccessible = true;
        val drawables = arrayOf(
                context.resources.getDrawableCompat(mCursorDrawableRes).apply { setColorFilter(color, PorterDuff.Mode.SRC_IN) },
                context.resources.getDrawableCompat(mCursorDrawableRes).apply { setColorFilter(color, PorterDuff.Mode.SRC_IN) }
        )
        fCursorDrawable.set(editor, drawables);
    } catch (ignored: Throwable) {
    }
}