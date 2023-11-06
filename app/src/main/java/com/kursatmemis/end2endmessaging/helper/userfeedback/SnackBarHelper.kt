package com.kursatmemis.end2endmessaging.helper.userfeedback

import android.view.View
import com.google.android.material.snackbar.Snackbar

data class SnackBarAction(val text: String, val listener: View.OnClickListener)

fun showSnackBar(
    view: View,
    text: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    snackBarAction: SnackBarAction? = null
) {
    if (snackBarAction == null) {
        Snackbar.make(view, text, duration).show()
    } else {
        Snackbar.make(view, text, duration).setAction(snackBarAction.text, snackBarAction.listener)
            .show()
    }
}

