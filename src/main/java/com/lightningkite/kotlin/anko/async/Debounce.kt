package com.lightningkite.kotlin.anko.async

/**
 * Created by jivie on 1/22/16.
 */

fun debounce(delay: Long, action: () -> Unit): () -> Unit = DebounceZero(delay, action)

fun <A> debounce(delay: Long, action: (A) -> Unit): (A) -> Unit = DebounceOne(delay, action)
fun <A, B> debounce(delay: Long, action: (A, B) -> Unit): (A, B) -> Unit = DebounceTwo(delay, action)
fun <A, B, C> debounce(delay: Long, action: (A, B, C) -> Unit): (A, B, C) -> Unit = DebounceThree(delay, action)

private class DebounceZero(val delay: Long, val action: () -> Unit) : () -> Unit {
    var posted = false
    val runnable = Runnable {
        posted = false
        action()
    }

    override fun invoke() {
        if (posted) {
            UIThread.uiHandler.removeCallbacks(runnable)
        }
        UIThread.uiHandler.postDelayed(runnable, delay)
        posted = true
    }
}

private class DebounceOne<A>(val delay: Long, val action: (A) -> Unit) : (A) -> Unit {

    var posted = false

    override fun invoke(a: A) {
        val runnable = Runnable {
            posted = false
            action(a)
        }
        if (posted) {
            UIThread.uiHandler.removeCallbacks(runnable)
        }
        UIThread.uiHandler.postDelayed(runnable, delay)
        posted = true
    }
}

private class DebounceTwo<A, B>(val delay: Long, val action: (A, B) -> Unit) : (A, B) -> Unit {

    var posted = false

    override fun invoke(a: A, b: B) {
        val runnable = Runnable {
            posted = false
            action(a, b)
        }
        if (posted) {
            UIThread.uiHandler.removeCallbacks(runnable)
        }
        UIThread.uiHandler.postDelayed(runnable, delay)
        posted = true
    }
}

private class DebounceThree<A, B, C>(val delay: Long, val action: (A, B, C) -> Unit) : (A, B, C) -> Unit {

    var posted = false

    override fun invoke(a: A, b: B, c: C) {
        val runnable = Runnable {
            posted = false
            action(a, b, c)
        }
        if (posted) {
            UIThread.uiHandler.removeCallbacks(runnable)
        }
        UIThread.uiHandler.postDelayed(runnable, delay)
        posted = true
    }
}