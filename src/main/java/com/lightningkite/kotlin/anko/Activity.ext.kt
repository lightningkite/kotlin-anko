package com.lightningkite.kotlin.anko

import android.content.Context
import org.jetbrains.anko.AlertDialogBuilder
import org.jetbrains.anko.selector

/**
 * Various extension functions for activities.
 * Created by jivie on 4/12/16.
 */
inline fun Context.selector(title: CharSequence?, pairs: List<Pair<CharSequence, () -> Unit>>) {
    selector(title, pairs.map { it.first }) { dialogInterface, someInt ->
        if(someInt >= 0) {
            pairs[someInt].second()
        }
//        if (it >= 0) {
//            pairs[it].second()
//        }
    }
}

inline fun Context.selector(title: CharSequence?, vararg pairs: Pair<CharSequence, () -> Unit>) {
    selector(title, pairs.map { it.first }) { dialogInterface, it ->
        if (it >= 0) {
            pairs[it].second()
        }
    }
}

inline fun Context.selector(title: Int?, pairs: List<Pair<Int, () -> Unit>>) {
    val titleString = if (title == null) null else resources.getString(title)
    selector(titleString, pairs.map { resources.getString(it.first) }) {  dialogInterface, it ->
        if (it >= 0) {
            pairs[it].second()
        }
    }
}

inline fun Context.selector(title: Int?, vararg pairs: Pair<Int, () -> Unit>) {
    val titleString = if (title == null) null else resources.getString(title)
    selector(titleString, pairs.map { resources.getString(it.first) }) {  dialogInterface, it ->
        if (it >= 0) {
            pairs[it].second()
        }
    }
}

inline fun Context.selector(title: Int?, vararg pairs: Pair<Int, () -> Unit>, crossinline onCancel: () -> Unit) {
    with(AlertDialogBuilder(this)) {
        if (title != null) title(title)
        items(pairs.map { resources.getString(it.first) }) {
            if (it >= 0) {
                pairs[it].second()
            }
        }
        this.onCancel {
            onCancel()
        }
        show()
    }
}