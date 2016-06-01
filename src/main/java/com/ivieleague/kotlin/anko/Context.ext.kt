package com.ivieleague.kotlin.anko

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

/**
 * Extension functions for Context
 * Created by jivie on 6/1/16.
 */

fun Context.getActivity(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return baseContext.getActivity()
    } else {
        return null
    }
}

fun Context.getUniquePreferenceId(): String {
    val key = "com.lightningkite.kotlincomponents.device.install_uuid"
    val found: String? = defaultSharedPreferences.getString(key, null)
    if (found != null) return found
    val made = UUID.randomUUID().toString()
    defaultSharedPreferences.edit().putString(key, made).apply()
    return made
}