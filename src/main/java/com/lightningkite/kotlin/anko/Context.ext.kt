package com.lightningkite.kotlin.anko

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import org.jetbrains.anko.AnkoContextImpl
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.inputMethodManager
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
    val sharedPreferences = getSharedPreferences("com.lightningkite.kotlin.anko", 0)
    val found: String? = sharedPreferences.getString(key, null)
    if (found != null) return found
    val made = UUID.randomUUID().toString()
    sharedPreferences.edit().putString(key, made).apply()
    return made
}

inline fun Context.timePicker(start: Calendar, crossinline after: (Calendar) -> Unit) {
    TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        start.set(Calendar.HOUR_OF_DAY, hourOfDay)
        start.set(Calendar.MINUTE, minute)
        after(start)
    }, start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), false).show()
}

inline fun Context.datePicker(start: Calendar, crossinline after: (Calendar) -> Unit) {
    DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                start.set(Calendar.YEAR, year)
                start.set(Calendar.MONTH, monthOfYear)
                start.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                after(start)
            },
            start.get(Calendar.YEAR),
            start.get(Calendar.MONTH),
            start.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun Context.hideSoftInput() {
    inputMethodManager.toggleSoftInput(0, 0)
}

fun <T> Context.anko(owner: T, setup: AnkoContextImpl<T>.() -> Unit): View {
    return AnkoContextImpl<T>(this, owner, false).apply(setup).view
}

fun Context.anko(setup: AnkoContextImpl<Context>.() -> Unit): View {
    return AnkoContextImpl(this, this, false).apply(setup).view
}

fun <T> Context.anko(owner: T): AnkoContextImpl<T> {
    return AnkoContextImpl<T>(this, owner, false)
}

fun Context.anko(): AnkoContextImpl<Context> {
    return AnkoContextImpl(this, this, false)
}

inline fun Context.isNetworkAvailable(): Boolean = connectivityManager.activeNetworkInfo?.isConnected ?: false