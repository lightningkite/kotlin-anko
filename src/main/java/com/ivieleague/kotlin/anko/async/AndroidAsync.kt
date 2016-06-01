package com.ivieleague.kotlin.anko.async

import android.os.Handler
import android.os.Looper
import com.ivieleague.kotlin.async.Async
import com.ivieleague.kotlin.async.AsyncInterface

/**
 * Various functions to do things asynchronously.
 * Created by jivie on 9/2/15.
 */

object AndroidAsync : AsyncInterface {
    val uiHandler: Handler = Handler(Looper.getMainLooper())

    override fun sendToThread(action: () -> Unit) {
        uiHandler.post(action)
    }

    fun init() {
        Async.uiThreadInterface = this
    }
}
