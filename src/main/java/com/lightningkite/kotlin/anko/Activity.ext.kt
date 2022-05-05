package com.lightningkite.kotlin.anko

import android.content.Context
import org.jetbrains.anko.Android
import org.jetbrains.anko.selector

/**
 * Various extension functions for activities.
 * Created by jivie on 4/12/16.
 */
inline fun Context.selector(title: CharSequence?, pairs: List<Pair<CharSequence, () -> Unit>>) {
    selector(title, pairs.map { it.first }) { _, it ->
        if (it >= 0) {
            pairs[it].second()
        }
    }
}

inline fun Context.selector(title: CharSequence?, vararg pairs: Pair<CharSequence, () -> Unit>) {
    selector(title, pairs.map { it.first }) { _, it ->
        if (it >= 0) {
            pairs[it].second()
        }
    }
}

inline fun Context.selector(title: Int?, pairs: List<Pair<Int, () -> Unit>>) {
    val titleString = if (title == null) null else resources.getString(title)
    selector(titleString, pairs.map { resources.getString(it.first) }) { _, it ->
        if (it >= 0) {
            pairs[it].second()
        }
    }
}

inline fun Context.selector(title: Int?, vararg pairs: Pair<Int, () -> Unit>) {
    val titleString = if (title == null) null else resources.getString(title)
    selector(titleString, pairs.map { resources.getString(it.first) }) { _, it ->
        if (it >= 0) {
            pairs[it].second()
        }
    }
}

inline fun Context.selector(titleResId: Int?, vararg pairs: Pair<Int, () -> Unit>, crossinline onCancel: () -> Unit) {
    with(Android(this)) {
        if (titleResId != null) titleResource = titleResId
        items(pairs.map { resources.getString(it.first) }) { _, it ->
            if (it >= 0) {
                pairs[it].second()
            }
        }
        onCancelled {
            onCancel()
        }
        show()
    }
}