package com.lightningkite.kotlin.anko

import android.support.annotation.StyleRes
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import org.jetbrains.anko.custom.ankoView

/**
 * Created by josep on 3/3/2016.
 */
var TextInputLayout.hintResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        hint = resources.getString(value)
    }
var TextInputLayout.errorResource: Int
    get() = throw IllegalAccessException()
    set(value) {
        error = resources.getString(value)
    }

inline fun TextInputLayout.textInputEditText(init: TextInputEditText.() -> Unit) = ankoView({
    TextInputEditText(it)
}, 0, init)

inline fun TextInputLayout.themedTextInputEditText(@StyleRes theme: Int, init: TextInputEditText.() -> Unit) = ankoView({
    TextInputEditText(it)
}, theme, init)