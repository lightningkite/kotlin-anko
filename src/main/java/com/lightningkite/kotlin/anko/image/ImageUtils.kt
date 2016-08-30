package com.lightningkite.kotlin.anko.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import com.lightningkite.kotlin.anko.files.fileSize
import com.lightningkite.kotlin.anko.files.getRealPath
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Functions for dealing with images.
 * Created by jivie on 8/14/15.
 */


/**
 * Rotates a bitmap, creating a new bitmap.  Beware of memory allocations.
 */
fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix()
    val w = width
    val h = height

    matrix.postRotate(degrees.toFloat())

    return Bitmap.createBitmap(this, 0, 0, w, h, matrix, true)
}

/**
 * Gets a bitmap from a Uri, scaling it down if necessary.
 */
fun Context.getBitmapFromUri(inputUri: Uri, maxWidth: Int, maxHeight: Int): Bitmap? {
    val initialBitmap = lessResolution(this, inputUri, maxWidth, maxHeight) ?: return null
    return correctBitmapRotation(initialBitmap, inputUri)
}

/**
 * Gets a bitmap from a Uri, scaling it down if necessary.
 */
fun Context.getBitmapFromUri(inputUri: Uri, minBytes: Long): Bitmap? {
    val initialBitmap = lessResolution(this, inputUri, minBytes) ?: return null
    return correctBitmapRotation(initialBitmap, inputUri)
}

/**
 * Corrects the rotation of a bitmap based on the EXIF tags in the file as specified by the URI
 */
private fun Context.correctBitmapRotation(initialBitmap: Bitmap, inputUri: Uri): Bitmap {
    var bitmap = initialBitmap
    try {
        val exif = ExifInterface(inputUri.getRealPath(this))
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                bitmap = initialBitmap.rotate(90)
                initialBitmap.recycle()
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                bitmap = initialBitmap.rotate(180)
                initialBitmap.recycle()
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                bitmap = initialBitmap.rotate(270)
                initialBitmap.recycle()
            }
        }
        //When choosing from the photos app on my phone it throws a IllegalArgumentException
        //saying that the filename is null.  But in this instance we don't need to change the
        //orientation and the bitmap is not null.
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: NullPointerException) {
        e.printStackTrace()
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }
    return bitmap
}


/**
 * Saves a bitmap to a file with a certain compression level between 0 and 100.
 */
private fun saveBitmap(outputFile: File, bitmap: Bitmap, compression: Int) {
    var out: FileOutputStream? = null
    try {
        Log.d("ImageFileManipulation", bitmap.toString())
        Log.d("ImageFileManipulation", outputFile.toString())
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compression, out)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (out != null) {
                out.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}

private fun lessResolution(context: Context, fileUri: Uri, width: Int, height: Int): Bitmap? {
    var inputStream: InputStream? = null
    try {
        val options = BitmapFactory.Options()

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inJustDecodeBounds = true

        inputStream = context.contentResolver.openInputStream(fileUri)
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream!!.close()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        inputStream = context.contentResolver.openInputStream(fileUri)
        val returnValue = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream!!.close()

        return returnValue

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (inputStream != null) {
                inputStream.close()
            }
        } catch (e: Exception) {
            /*squish*/
        }

    }
    return null
}


private fun lessResolution(context: Context, fileUri: Uri, minBytes: Long): Bitmap? {
    var inputStream: InputStream? = null
    try {
        val options = BitmapFactory.Options()

        // Calculate inSampleSize
        val size = context.contentResolver.fileSize(fileUri) ?: minBytes
        options.inSampleSize = ImageUtils.calculateInSampleSize(size, minBytes)

        inputStream = context.contentResolver.openInputStream(fileUri)
        val returnValue = BitmapFactory.decodeStream(inputStream, null, options)
        inputStream!!.close()

        return returnValue

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            if (inputStream != null) {
                inputStream.close()
            }
        } catch (e: Exception) {
            /*squish*/
        }

    }
    return null
}

fun BitmapFactory_decodeByteArraySized(array: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap {
    val measureOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(array, 0, array.size, measureOptions)
    val options = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(measureOptions, reqWidth, reqHeight)
    }
    return BitmapFactory.decodeByteArray(array, 0, array.size, options)
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        // Calculate ratios of height and width to requested height and width
        val heightRatio = Math.ceil(height.toDouble() / reqHeight.toDouble()).toInt()
        val widthRatio = Math.ceil(width.toDouble() / reqWidth.toDouble()).toInt()

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions be just smaller than or equal to the
        // requested height and width.
        inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}

@Suppress("NOTHING_TO_INLINE")
inline fun calculateInSampleSizeMax(options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int): Int {
    var inSampleSize = 1

    if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
        // Calculate ratios of height and width to requested height and width
        val heightRatio = Math.ceil(options.outHeight / maxHeight.toDouble()).toInt()
        val widthRatio = Math.ceil(options.outWidth / maxWidth.toDouble()).toInt()

        // Choose the bigger ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions smaller than or equal to the
        // requested height and width.
        inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}

/**
 * Created by jivie on 6/30/16.
 */
object ImageUtils {
    fun calculateInSampleSize(length: Long, minBytes: Long): Int {
        return Math.ceil(length.toDouble() / minBytes).toInt()
    }

}