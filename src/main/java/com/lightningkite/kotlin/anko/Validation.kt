package com.lightningkite.kotlin.anko

import com.google.android.material.textfield.TextInputLayout
import android.view.View

/**
 * Created by joseph on 8/1/16.
 *
 * Can be used on forms to easily do validation.
 *
 * Make a collection of these and use the extension functions below.
 */
class Validation(
        val view: View? = null,
        val validator:()->String? = {null}
){
    fun getIssue(): ValidationIssue?{
        return ValidationIssue(view, validator() ?: return null)
    }

    operator fun component1():View? = view
    operator fun component2():View? = view
}
class ValidationIssue(
    val view: View? = null,
    val message:String = ""
)

fun Collection<Validation>.issues(): List<ValidationIssue> = mapNotNull { it.getIssue() }
fun Collection<Validation>.firstIssue(): ValidationIssue? = mapNotNull { it.getIssue() }.firstOrNull()

fun Collection<Validation>.validOrSnackbar(snackView:View):Boolean{
    val issues = issues()
    if(issues.isEmpty()) return true

    for((view, validator) in this){
        if(view is TextInputLayout){
            view.error = null
        }
    }
    for(issue in issues){
        val view = issue.view
        if(view is TextInputLayout){
            view.error = issue.message
        }
    }

    val error = issues.first()
    error.view?.requestFocus()
    snackView.snackbar(error.message)

    return false
}

fun MutableCollection<Validation>.quickAdd(view:View, errorResource:Int, check:()->Boolean){
    add(Validation(view, {
        if(!check()){
            view.resources.getString(errorResource)
        } else {
            null
        }
    }))
}

fun MutableCollection<Validation>.quickAdd(view: View, errorString: String, check: () -> Boolean){
    add(Validation(view, {
        if(!check()){
            errorString
        } else {
            null
        }
    }))
}