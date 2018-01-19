package com.lightningkite.kotlin.anko

import android.view.View
import com.lightningkite.kotlin.anko.activity.ActivityAccess


typealias ViewGenerator<T> = (ActivityAccess, T) -> View