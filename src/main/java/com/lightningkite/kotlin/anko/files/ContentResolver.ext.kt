package com.lightningkite.kotlin.anko.files

import android.content.ContentResolver
import android.net.Uri

/**
 * Created by jivie on 4/12/16.
 */
fun ContentResolver.fileSize(uri: Uri): Long? {
    return openFileDescriptor(uri, "r")?.statSize
}