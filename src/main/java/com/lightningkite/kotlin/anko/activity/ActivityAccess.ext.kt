package com.lightningkite.kotlin.anko.activity

import android.content.Intent
import android.os.Bundle
import com.lightningkite.kotlin.anko.anko
import org.jetbrains.anko.AnkoContextImpl
import org.jetbrains.anko.bundleOf

fun ActivityAccess.startIntent(intent: Intent, options: Bundle = bundleOf(), onResult: (Int, Intent?) -> Unit = { _, _ -> }) {
    activity?.startActivityForResult(intent, prepareOnResult(onResult = onResult), options)
}

inline fun ActivityAccess.anko(setup: AnkoContextImpl<ActivityAccess>.() -> Unit) = context.anko(this, setup)