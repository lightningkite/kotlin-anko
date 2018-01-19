package com.lightningkite.kotlin.anko.activity

import android.content.Intent
import android.os.Bundle
import org.jetbrains.anko.bundleOf

fun ActivityAccess.startIntent(intent: Intent, options: Bundle = bundleOf(), onResult: (Int, Intent?) -> Unit = { _, _ -> }) {
    activity?.startActivityForResult(intent, prepareOnResult(onResult = onResult), options)
}