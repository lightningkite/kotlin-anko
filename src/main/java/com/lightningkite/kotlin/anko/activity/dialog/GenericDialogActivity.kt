package com.lightningkite.kotlin.anko.activity.dialog

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.lightningkite.kotlin.anko.activity.AccessibleActivity
import com.lightningkite.kotlin.anko.activity.ActivityAccess
import java.util.*

/**
 * An activity for creating dialogs of any sort.
 * More stable and flexible than default alerts.
 *
 * Created by joseph on 1/22/18.
 */
open class GenericDialogActivity : AccessibleActivity() {

    data class ContainerData(
            val viewGenerator: (ActivityAccess) -> View,
            val layoutParamsSetup: WindowManager.LayoutParams.() -> Unit,
            val windowModifier: Window.() -> Unit = {}
    )

    companion object {
        const val EXTRA_CONTAINER: String = "GenericDialogActivity.containerId"
        const val EXTRA_DISMISS_ON_TOUCH_OUTSIDE: String = "GenericDialogActivity.dismissOnTouchOutside"
        val containers: HashMap<Int, ContainerData> = HashMap()
    }

    var myIndex = 0
    var myContainerData: ContainerData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        myIndex = intent.getIntExtra(EXTRA_CONTAINER, 0)
        myContainerData = containers[myIndex]
        if (myContainerData != null) {
            setContentView(myContainerData!!.viewGenerator.invoke(this))
            setFinishOnTouchOutside(intent.getBooleanExtra(EXTRA_DISMISS_ON_TOUCH_OUTSIDE, true))
        } else {
            finish()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (myContainerData != null) {
            window.apply(myContainerData!!.windowModifier)
            windowManager.updateViewLayout(
                    window.decorView,
                    (window.decorView.layoutParams as WindowManager.LayoutParams)
                            .apply(myContainerData!!.layoutParamsSetup))
        }
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra(EXTRA_DISMISS_ON_TOUCH_OUTSIDE, true)) {
            super.onBackPressed()
        }
    }

    override fun finish() {
        containers.remove(myIndex)
        super.finish()
    }
}