package com.ivieleague.kotlin.anko.async

import android.os.Handler
import android.os.Looper
import com.ivieleague.kotlin.async.Async

/**
 * Various functions to do things asynchronously.
 * Created by jivie on 9/2/15.
 */

object AndroidAsync {
    val uiHandler: Handler = Handler(Looper.getMainLooper())
}


/**
 * Runs [action] asynchronously with its result being dealt with on the UI thread in [uiThread].
 * @param action The lambda to run asynchronously.
 * @param uiThread The lambda to run with the result of [action] on the UI thread.
 */
fun <T> doAsync(action: () -> T, uiThread: (T) -> Unit) {
    Async.threadPool.execute({
        try {
            val result = action()
            AndroidAsync.uiHandler.post {
                uiThread(result)
            }
        } catch(e: Exception) {
            AndroidAsync.uiHandler.post {
                throw e
            }
        }
    })
}

/**
 * Posts [action] to the main thread.
 * @param action The lambda to run asynchronously.
 */
fun doUiThread(delay: Long = 0, action: () -> Unit) {
    AndroidAsync.uiHandler.postDelayed(action, delay)
}