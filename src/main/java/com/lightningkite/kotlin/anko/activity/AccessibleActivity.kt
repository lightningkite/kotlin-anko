package com.lightningkite.kotlin.anko.activity

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.lightningkite.kotlin.lambda.invokeAll
import java.util.*

/**
 * All activities hosting [ViewController]s must be extended from this one.
 * It handles the calling of other activities with [onActivityResult], the attaching of a
 * [VCContainer], and use the back button on the [VCContainer].
 * Created by jivie on 10/12/15.
 */
abstract class AccessibleActivity : AppCompatActivity(), ActivityAccess {

    override val activity: Activity?
        get() = this
    override val context: Context
        get() = this

    var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }

    override val onResume = HashSet<() -> Unit>()
    override fun onResume() {
        super.onResume()
        onResume.invokeAll()
    }

    override val onPause = HashSet<() -> Unit>()
    override fun onPause() {
        onPause.invokeAll()
        super.onPause()
    }

    override val onSaveInstanceState = HashSet<(outState: Bundle) -> Unit>()
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveInstanceState.invokeAll(outState)
    }

    override val onLowMemory = HashSet<() -> Unit>()
    override fun onLowMemory() {
        super.onLowMemory()
        onLowMemory.invokeAll()
    }

    override val onBackPressed = ArrayList<() -> Boolean>()
    override fun onBackPressed() {
        if (!onBackPressed.reversed().any { it.invoke() }) {
            super.onBackPressed()
        }
    }

    override val onDestroy = HashSet<() -> Unit>()
    override fun onDestroy() {
        onDestroy.invokeAll()
        super.onDestroy()
    }

    val requestReturns: HashMap<Int, (Map<String, Int>) -> Unit> = HashMap()

    companion object {
        val returns: HashMap<Int, (Int, Intent?) -> Unit> = HashMap()
    }

    override val onActivityResult = ArrayList<(Int, Int, Intent?) -> Unit>()

    override fun prepareOnResult(presetCode: Int, onResult: (Int, Intent?) -> Unit): Int {
        returns[presetCode] = onResult
        return presetCode
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        onActivityResult.invokeAll(requestCode, resultCode, data)
        returns[requestCode]?.invoke(resultCode, data)
        returns.remove(requestCode)
    }

    /**
     * Requests a bunch of permissions and returns a map of permissions that were previously ungranted and their new status.
     */
    override fun requestPermissions(permission: Array<String>, onResult: (Map<String, Int>) -> Unit) {
        val ungranted = permission.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungranted.isNotEmpty()) {
            val generated: Int = (Math.random() * 0xFFFF).toInt()

            requestReturns[generated] = onResult

            ActivityCompat.requestPermissions(this, ungranted.toTypedArray(), generated)

        } else {
            onResult(emptyMap())
        }
    }

    /**
     * Requests a single permissions and returns whether it was granted or not.
     */
    override fun requestPermission(permission: String, onResult: (Boolean) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            val generated: Int = (Math.random() * 0xFFFF).toInt()
            requestReturns[generated] = {
                onResult(it[permission] == PackageManager.PERMISSION_GRANTED)
            }
            ActivityCompat.requestPermissions(this, arrayOf(permission), generated)

        } else {
            onResult(true)
        }
    }

    @TargetApi(23)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Build.VERSION.SDK_INT >= 23) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            val map = HashMap<String, Int>()
            for (i in permissions.indices) {
                map[permissions[i]] = grantResults[i]
            }
            requestReturns[requestCode]?.invoke(map)

            requestReturns.remove(requestCode)
        }
    }
}