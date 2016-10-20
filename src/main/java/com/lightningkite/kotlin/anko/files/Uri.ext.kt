package com.lightningkite.kotlin.anko.files

import android.content.Context
import android.net.Uri
import com.lightningkite.kotlin.stream.toByteArray

/**
 * Various functions to use on Uri's.
 * Created by jivie on 6/30/16.
 */


/**
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun Uri.isExternalStorageDocument(): Boolean {
    return "com.android.externalstorage.documents" == authority
}

/**
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun Uri.isDownloadsDocument(): Boolean {
    return "com.android.providers.downloads.documents" == authority
}

/**
 * @return Whether the Uri authority is MediaProvider.
 */
fun Uri.isMediaDocument(): Boolean {
    return "com.android.providers.media.documents" == authority
}

fun Uri.toByteArray(context: Context): ByteArray {
    return context.contentResolver.openInputStream(this).toByteArray()
}