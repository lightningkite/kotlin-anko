package com.lightningkite.kotlin.anko

import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.lightningkite.kotlin.Disposable
import com.lightningkite.kotlin.anko.image.getBitmapFromUri
import com.lightningkite.kotlin.async.doAsync
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageResource
import java.util.*

/**
 * Created by joseph on 10/14/16.
 */

val ImageView_previousBitmap: MutableMap<ImageView, Disposable> = HashMap()

fun ImageView.imageLocalUri(uri: Uri, minBytes: Long, brokenImageResource: Int? = null, onResult: (Disposable?) -> Unit) {
//    imageStreamCustom(request, howToStream = {
//        val holder = bitmapExif(context, minBytes)
//        if (holder == null) null else holder.bitmap to holder
//    }, brokenImageResource = brokenImageResource, onResult = onResult)
    doAsync({
        context.getBitmapFromUri(uri, minBytes)
    }, {
        if (it == null) {
            //It failed, so use the broken image.
            if (brokenImageResource != null) {
                imageResource = brokenImageResource
            }
            onResult(null)
        } else {
            //cancel if this isn't attached.
            if (!isAttachedToWindowCompat()) {
                it.recycle()
                return@doAsync
            }

            //setup a listener with disposal for recycling the image and removing itself
            val newListener = object : View.OnAttachStateChangeListener, Disposable {
                var disposed = false
                override fun onViewDetachedFromWindow(v: View?) {
                    dispose()
                }

                override fun onViewAttachedToWindow(v: View?) {
                }

                override fun dispose() {
                    if (disposed) return else disposed = true
                    setImageDrawable(null)
                    it.recycle()
                    ImageView_previousBitmap.remove(this@imageLocalUri)
                    removeOnAttachStateChangeListener(this)
                }
            }

            //Remove old listener of this kind
            ImageView_previousBitmap[this]?.dispose()

            //set the image
            imageBitmap = it

            //add new listener
            ImageView_previousBitmap[this] = newListener
            addOnAttachStateChangeListener(newListener)

            //Return the listener as a disposable to dispose the image on demand
            onResult(newListener)
        }
    })
}

fun ImageView.imageLocalUri(uri: Uri, maxWidth: Int = 2048, maxHeight: Int = 2048, brokenImageResource: Int? = null, onResult: (Disposable?) -> Unit) {
//    imageStreamCustom(request, howToStream = {
//        val holder = bitmapExif(context, minBytes)
//        if (holder == null) null else holder.bitmap to holder
//    }, brokenImageResource = brokenImageResource, onResult = onResult)
    doAsync({
        context.getBitmapFromUri(uri, maxWidth, maxHeight)
    }, {
        if (it == null) {
            //It failed, so use the broken image.
            if (brokenImageResource != null) {
                imageResource = brokenImageResource
            }
            onResult(null)
        } else {
            //cancel if this isn't attached.
            if (!isAttachedToWindowCompat()) {
                it.recycle()
                return@doAsync
            }

            //setup a listener with disposal for recycling the image and removing itself
            val newListener = object : View.OnAttachStateChangeListener, Disposable {
                var disposed = false
                override fun onViewDetachedFromWindow(v: View?) {
                    dispose()
                }

                override fun onViewAttachedToWindow(v: View?) {
                }

                override fun dispose() {
                    if (disposed) return else disposed = true
                    setImageDrawable(null)
                    it.recycle()
                    ImageView_previousBitmap.remove(this@imageLocalUri)
                    removeOnAttachStateChangeListener(this)
                }
            }

            //Remove old listener of this kind
            ImageView_previousBitmap[this]?.dispose()

            //set the image
            imageBitmap = it

            //add new listener
            ImageView_previousBitmap[this] = newListener
            addOnAttachStateChangeListener(newListener)

            //Return the listener as a disposable to dispose the image on demand
            onResult(newListener)
        }
    })
}