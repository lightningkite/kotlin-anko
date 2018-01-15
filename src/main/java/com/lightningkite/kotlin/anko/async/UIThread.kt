package com.lightningkite.kotlin.anko.async

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * Various functions to do things asynchronously.
 * Created by jivie on 9/2/15.
 */

object UIThread : Executor {
    val uiHandler: Handler = Handler(Looper.getMainLooper())

    override fun execute(p0: Runnable) {
        uiHandler.post(p0)
    }

}
