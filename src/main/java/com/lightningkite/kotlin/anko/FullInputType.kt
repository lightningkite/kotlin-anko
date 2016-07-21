package com.lightningkite.kotlin.anko

import android.text.InputType

/**
 * Created by jivie on 6/17/16.
 */
object FullInputType {

    const val NAME: Int = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
    const val INTEGER: Int = InputType.TYPE_CLASS_NUMBER
}
