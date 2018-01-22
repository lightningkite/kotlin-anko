package com.lightningkite.kotlin.anko.activity.dialog

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.lightningkite.kotlin.anko.*
import com.lightningkite.kotlin.anko.activity.ActivityAccess
import org.jetbrains.anko.*

/**
 * Opens an instance of [GenericDialogActivity].
 */
fun Context.dialog(
        dismissOnTouchOutside: Boolean = true,
        windowModifier: Window.() -> Unit = {},
        layoutParamModifier: WindowManager.LayoutParams.() -> Unit = {},
        viewGenerator: (ActivityAccess) -> View
) {
    val id: Int = viewGenerator.hashCode()
    GenericDialogActivity.containers[id] = GenericDialogActivity.ContainerData(viewGenerator, layoutParamModifier, windowModifier)
    startActivity(Intent(this, GenericDialogActivity::class.java).apply {
        putExtra(GenericDialogActivity.EXTRA_CONTAINER, id)
        putExtra(GenericDialogActivity.EXTRA_DISMISS_ON_TOUCH_OUTSIDE, dismissOnTouchOutside)
    })
}

/**
 * Opens an instance of [GenericDialogActivity].
 */
inline fun Context.dialogAnko(
        dismissOnTouchOutside: Boolean = true,
        noinline windowModifier: Window.() -> Unit = {},
        noinline layoutParamModifier: WindowManager.LayoutParams.() -> Unit = {},
        crossinline viewMaker: AnkoContext<ActivityAccess>.() -> View
) = dialog(
        dismissOnTouchOutside = dismissOnTouchOutside,
        windowModifier = windowModifier,
        layoutParamModifier = layoutParamModifier,
        viewGenerator = { anko(it) { viewMaker.invoke(this) } }
)


private inline fun ViewGroup.MarginLayoutParams.standardMargins(ctx: Context) {
    leftMargin = ctx.dip(16)
    rightMargin = ctx.dip(16)
    topMargin = ctx.dip(8)
    bottomMargin = ctx.dip(8)
}

private inline fun TextView.styleTitle() {
    textSize = 18f
    setTypeface(null, Typeface.BOLD)
    textColorResource = android.R.color.primary_text_light
}

private inline fun TextView.styleMessage() {
    textSize = 16f
    textColorResource = android.R.color.secondary_text_light
}

private inline fun Button.styleNormal() {
    textSize = 16f
    textColorResource = android.R.color.secondary_text_light
    setAllCaps(true)
    backgroundResource = selectableItemBackgroundBorderlessResource
}

private inline fun Button.styleDestructive() {
    textSize = 16f
    textColor = Color.RED
    setAllCaps(true)
}

object StandardDialog {
    fun okButton(resources: Resources, okResource: Int = android.R.string.ok, action: () -> Unit = {}): Pair<String, (ActivityAccess) -> Unit> =
            resources.getString(okResource) to { it: ActivityAccess -> action(); it.activity?.finish(); Unit }

    fun cancelButton(resources: Resources, cancelResource: Int = android.R.string.cancel, action: () -> Unit = {}): Pair<String, (ActivityAccess) -> Unit> =
            resources.getString(cancelResource) to { it: ActivityAccess -> action(); it.activity?.finish(); Unit }

    fun cancelButton(resources: Resources): Pair<String, (ActivityAccess) -> Unit> = resources.getString(android.R.string.cancel) to { it: ActivityAccess -> it.activity?.finish(); Unit }


    inline fun ViewGroup.MarginLayoutParams.standardMargins(ctx: Context) {
        leftMargin = ctx.dip(16)
        rightMargin = ctx.dip(16)
        topMargin = ctx.dip(8)
        bottomMargin = ctx.dip(8)
    }

    inline fun TextView.styleTitle() {
        textSize = 18f
        setTypeface(null, Typeface.BOLD)
        textColorResource = android.R.color.primary_text_light
    }

    inline fun TextView.styleMessage() {
        textSize = 16f
        textColorResource = android.R.color.secondary_text_light
    }

    inline fun Button.styleNormal() {
        textSize = 16f
        textColorResource = android.R.color.secondary_text_light
        setAllCaps(true)
        backgroundResource = selectableItemBackgroundBorderlessResource
    }

    private inline fun Button.styleDestructive() {
        textSize = 16f
        textColor = Color.RED
        setAllCaps(true)
    }
}

fun Context.alertDialog(message: Int) = standardDialog(
        null,
        resources.getString(message),
        listOf(StandardDialog.okButton(resources) {}),
        true,
        null
)

fun Context.standardDialog(
        title: Int?,
        message: Int?,
        buttons: List<Pair<String, (ActivityAccess) -> Unit>>,
        dismissOnClickOutside: Boolean = true,
        content: (ViewGroup.(ActivityAccess) -> View)? = null
) = standardDialog(
        if (title != null) resources.getString(title) else null,
        if (message != null) resources.getString(message) else null,
        buttons,
        dismissOnClickOutside,
        content
)

object CustomDialog {
    fun okButton(resources: Resources, okResource: Int = android.R.string.ok, action: () -> Unit = {}, okStyle: (Button) -> Unit): Triple<String, (ActivityAccess) -> Unit, (Button) -> Unit> =
            Triple(resources.getString(okResource), { it: ActivityAccess -> action(); it.activity?.finish(); Unit }, okStyle)

    fun cancelButton(resources: Resources, cancelResource: Int = android.R.string.cancel, action: () -> Unit = {}, cancelStyle: (Button) -> Unit): Triple<String, (ActivityAccess) -> Unit, (Button) -> Unit> =
            Triple(resources.getString(cancelResource), { it: ActivityAccess -> action(); it.activity?.finish(); Unit }, cancelStyle)
}

/**
 * Creates a psuedo-dialog that is actually an activity.  Significantly more stable and safe.
 */
fun Context.standardDialog(
        title: String?,
        message: String?,
        buttons: List<Pair<String, (ActivityAccess) -> Unit>>,
        dismissOnClickOutside: Boolean = true,
        content: (ViewGroup.(ActivityAccess) -> View)? = null
) {
    return dialogAnko(dismissOnClickOutside, layoutParamModifier = { width = matchParent }) {
        scrollView {
            verticalLayout {
                //title
                textView(text = title) {
                    styleTitle()
                    if (title.isNullOrEmpty()) {
                        visibility = View.GONE
                    }
                }.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                    topMargin = dip(16)
                }

                //message
                textView(text = message) {
                    styleMessage()
                    if (message.isNullOrEmpty()) {
                        visibility = View.GONE
                    }
                }.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                }

                //custom content
                content?.invoke(this, owner)?.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                }

                //buttons
                linearLayout {
                    gravity = Gravity.END
                    for ((buttonName, action) in buttons) {
                        button(buttonName) {
                            styleNormal()
                            setOnClickListener {
                                action(owner)
                            }
                        }.lparams(wrapContent, wrapContent) {
                            standardMargins(context)
                        }
                    }
                }.lparams(matchParent, wrapContent)
            }
        }
    }
}

fun Context.customDialog(
        title: Int?,
        message: Int,
        buttons: List<Triple<String, (ActivityAccess) -> Unit, (Button) -> Unit>>,
        dismissOnClickOutside: Boolean = true,
        content: (ViewGroup.(ActivityAccess) -> View)? = null
) = customDialog(
        if (title != null) resources.getString(title) else null,
        resources.getString(message),
        buttons,
        dismissOnClickOutside,
        content
)

/**
 * Creates a psuedo-dialog that is actually an activity.  Significantly more stable and safe.
 */
fun Context.customDialog(
        title: String?,
        message: String,
        buttons: List<Triple<String, (ActivityAccess) -> Unit, (Button) -> Unit>>,
        dismissOnClickOutside: Boolean = true,
        content: (ViewGroup.(ActivityAccess) -> View)? = null
) {
    return dialogAnko(dismissOnClickOutside, layoutParamModifier = { width = matchParent }) {
        scrollView {
            verticalLayout {
                //title
                textView(text = title) {
                    styleTitle()
                    if (title.isNullOrEmpty()) {
                        visibility = View.GONE
                    }
                }.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                    topMargin = dip(16)
                }

                //message
                textView(text = message) {
                    styleMessage()
                }.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                }

                //custom content
                content?.invoke(this, owner)?.lparams(matchParent, wrapContent) {
                    standardMargins(context)
                }

                //buttons
                linearLayout {
                    gravity = Gravity.END
                    buttons.forEach { triple ->
                        button(triple.first) {
                            setOnClickListener { triple.second(owner) }
                            triple.third.invoke(this)
                        }.lparams {
                            standardMargins(context)
                        }
                    }
                }.lparams(matchParent, wrapContent)
            }
        }
    }
}

fun Context.confirmationDialog(title: Int? = null, message: Int, onCancel: () -> Unit = {}, onConfirm: () -> Unit) {
    return standardDialog(title, message, listOf(StandardDialog.okButton(resources, action = onConfirm), StandardDialog.cancelButton(resources, action = onCancel)))
}

fun Context.confirmationDialog(title: String? = null, message: String, onCancel: () -> Unit = {}, onConfirm: () -> Unit) {
    return standardDialog(title, message, listOf(StandardDialog.okButton(resources, action = onConfirm), StandardDialog.cancelButton(resources, action = onCancel)))
}

fun Context.confirmationDialog(title: String? = null, message: String, okResource: Int = android.R.string.ok, cancelResource: Int = android.R.string.cancel, dismissOnClickOutside: Boolean = true, onPositiveAction: () -> Unit, onNegativeAction: () -> Unit) {
    return standardDialog(
            title,
            message,
            listOf(StandardDialog.okButton(resources, okResource, onPositiveAction), StandardDialog.cancelButton(resources, cancelResource, onNegativeAction)),
            dismissOnClickOutside = dismissOnClickOutside)
}

fun Context.confirmationDialog(title: Int? = null, message: Int, okResource: Int = android.R.string.ok, cancelResource: Int = android.R.string.cancel, dismissOnClickOutside: Boolean = true, onPositiveAction: () -> Unit, onNegativeAction: () -> Unit) {
    return standardDialog(
            title,
            message,
            listOf(StandardDialog.okButton(resources, okResource, onPositiveAction), StandardDialog.cancelButton(resources, cancelResource, onNegativeAction)),
            dismissOnClickOutside = dismissOnClickOutside)
}

fun Context.customConfirmationDialog(title: Int? = null, message: Int, okResource: Int = android.R.string.ok, cancelResource: Int = android.R.string.cancel, dismissOnClickOutside: Boolean = true, onPositiveAction: () -> Unit, onNegativeAction: () -> Unit, okStyle: Button.() -> Unit, cancelStyle: Button.() -> Unit) {
    return customDialog(
            title,
            message,
            listOf(CustomDialog.okButton(resources, okResource, onPositiveAction, okStyle), CustomDialog.cancelButton(resources, cancelResource, onNegativeAction, cancelStyle)),
            dismissOnClickOutside = dismissOnClickOutside
    )
}

fun Context.infoDialog(title: Int? = null, message: Int?, content: (ViewGroup.(ActivityAccess) -> View)? = null, onConfirm: () -> Unit = {}) {
    return standardDialog(title, message, listOf(StandardDialog.okButton(resources, action = onConfirm)), content = content)
}

fun Context.infoDialog(title: String? = null, message: String?, content: (ViewGroup.(ActivityAccess) -> View)? = null, onConfirm: () -> Unit = {}) {
    return standardDialog(title, message, listOf(StandardDialog.okButton(resources, action = onConfirm)), content = content)
}


/**
 * Creates a dialog with an input text field on it.
 */
fun Context.inputDialog(
        title: Int,
        message: Int,
        hint: Int = 0,
        defaultValue: String = "",
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        canCancel: Boolean = true,
        validation: (String) -> Int? = { null },
        onResult: (String?) -> Unit
) {
    return inputDialog(
            title = resources.getString(title),
            message = resources.getString(message),
            hint = if (hint == 0) "" else resources.getString(hint),
            defaultValue = defaultValue,
            inputType = inputType,
            canCancel = canCancel,
            validation = validation,
            onResult = onResult
    )
}


/**
 * Creates a dialog with an input text field on it.
 */
fun Context.inputDialog(
        title: String,
        message: String,
        hint: String = "",
        defaultValue: String = "",
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        canCancel: Boolean = true,
        validation: (String) -> Int? = { null },
        onResult: (String?) -> Unit
) {
    var et: EditText? = null
    standardDialog(
            title,
            message,
            listOf(
                    resources.getString(android.R.string.cancel)!! to { it: ActivityAccess ->
                        et?.hideSoftInput()
                        onResult(null)
                        it.activity?.finish()
                        Unit
                    },
                    resources.getString(android.R.string.ok)!! to { it: ActivityAccess ->
                        if (et != null) {
                            et?.hideSoftInput()
                            val result = et!!.text.toString()
                            val error = validation(result)
                            if (error == null) {
                                onResult(result)
                                it.activity?.finish()
                                Unit
                            } else {
                                snackbar(error)
                            }
                        }
                    }
            ),
            canCancel,
            {
                et = editText() {
                    this.setText(defaultValue)
                    this.inputType = inputType
                    this.hint = hint
                }
                et!!
            }
    )
}