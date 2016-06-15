package com.lightningkite.kotlin.anko.files

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Created by jivie on 4/12/16.
 */
fun ContentResolver.fileSize(uri: Uri): Long? {
    val cursor = query(uri, null, null, null, null) ?: return null
    cursor.moveToFirst()
    val result = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
    cursor.close()
    return result
}