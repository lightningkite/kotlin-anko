package com.lightningkite.kotlin.anko

import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.lightningkite.kotlin.Disposable
import com.lightningkite.kotlin.anko.files.fileSize
import com.lightningkite.kotlin.anko.image.ImageUtils
import com.lightningkite.kotlin.async.doAsync
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageResource
import java.util.*

/**
 * Created by joseph on 10/14/16.
 */

val ImageView_previousBitmap: MutableMap<ImageView, Disposable> = HashMap()

fun ImageView.imageUri(uri: Uri, minBytes: Long, brokenImageResource: Int? = null, onResult: (Disposable?) -> Unit) {
//    imageStreamCustom(request, howToStream = {
//        val holder = bitmapExif(context, minBytes)
//        if (holder == null) null else holder.bitmap to holder
//    }, brokenImageResource = brokenImageResource, onResult = onResult)
    doAsync({
        try {
            val bytes = context.contentResolver.fileSize(uri) ?: throw Exception("failed to get size")
            val stream = context.contentResolver.openInputStream(uri)
            try {
                BitmapFactory.decodeStream(stream, Rect(), BitmapFactory.Options().apply {
                    inSampleSize = ImageUtils.calculateInSampleSize(bytes, minBytes)
                })
            } finally {
                stream.close()
            }
        } catch(e: Exception) {
            e.printStackTrace()
            null
        }
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
                    ImageView_previousBitmap.remove(this@imageUri)
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