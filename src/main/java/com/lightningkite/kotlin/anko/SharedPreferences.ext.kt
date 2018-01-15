package com.lightningkite.kotlin.anko

import android.annotation.SuppressLint
import android.content.SharedPreferences

/**
 * Extensions for SharedPreferences
 *
 * Created by jivie on 6/3/16.
 */

@SuppressLint("ApplySharedPref")
/**
 * Returns true only the first time it is called for a particular key.
 * Can be reset by deleting the given key.
 */
fun SharedPreferences.once(key: String): Boolean {
    if (getBoolean(key, false)) {
        return false
    } else {
        edit().putBoolean(key, true).commit()
        return true
    }
}