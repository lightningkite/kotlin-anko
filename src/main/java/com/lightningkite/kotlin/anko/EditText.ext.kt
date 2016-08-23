package com.lightningkite.kotlin.anko

import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
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

fun EditText.onDone(action: (text: String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
        if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action(text.toString())
            return@OnKeyListener true;
        }
        false
    })
    setOnEditorActionListener({ v, actionId, event ->
        action(text.toString())
        true;
    })
}

fun EditText.onSend(action: (text: String) -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_SEND
    setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
        if ((event?.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action(text.toString())
            return@OnKeyListener true;
        }
        false
    })
    setOnEditorActionListener({ v, actionId, event ->
        action(text.toString())
        true;
    })
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

data class EditTextChangeData(
        var before: String = "",
        var insertionPoint: Int = 0,
        var replacedCount: Int = 0,
        var replacement: String = "",
        var after: String = "",
        var beforeSelectionStart: Int = 0,
        var beforeSelectionEnd: Int = 0
) {
    val replaced: String get() = before.substring(insertionPoint, replacedCount)
    val afterSelectionStart: Int get() = if (beforeSelectionStart < insertionPoint)
        beforeSelectionStart
    else
        beforeSelectionStart + replacement.length
    val afterSelectionEnd: Int get() = if (beforeSelectionEnd < insertionPoint)
        beforeSelectionEnd
    else
        beforeSelectionEnd + replacement.length
}

fun EditText.textChanger(listener: (change: EditTextChangeData) -> Pair<String, IntRange>) {
    addTextChangedListener(object : TextWatcher {

        val change = EditTextChangeData()

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            change.before = s.toString()
            change.insertionPoint = start
            change.replacedCount = count
            change.beforeSelectionStart = selectionStart
            change.beforeSelectionEnd = selectionEnd
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            change.replacement = s.substring(start, start + count)
            change.after = s.toString()
        }

        var ignore: Boolean = false
        override fun afterTextChanged(s: Editable) {
            if (ignore) {
                ignore = false
                return
            }
            ignore = true
            val (textResult, rangeResult) = listener(change)
            setText(textResult)
            setSelection(rangeResult.start, rangeResult.endInclusive)
            ignore = false
        }

    })
}

fun EditText.textListener(listener: (change: EditTextChangeData) -> Unit) {
    addTextChangedListener(object : TextWatcher {

        val change = EditTextChangeData()

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            change.before = s.toString()
            change.insertionPoint = start
            change.replacedCount = count
            change.beforeSelectionStart = selectionStart
            change.beforeSelectionEnd = selectionEnd
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            change.replacement = s.substring(start, start + count)
            change.after = s.toString()
        }

        override fun afterTextChanged(s: Editable) {
            listener(change)
        }

    })
}