package com.lightningkite.kotlin.anko

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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