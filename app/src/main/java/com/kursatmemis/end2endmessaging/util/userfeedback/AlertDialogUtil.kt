package com.kursatmemis.end2endmessaging.util.userfeedback

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import androidx.appcompat.app.AlertDialog

data class AlertDialogButton(
    val positiveButton: AlertDialogPositiveButton? = null,
    val negativeButton: AlertDialogNegativeButton? = null
)

data class AlertDialogPositiveButton(
    val positiveButtonText: String,
    val positiveButtonListener: DialogInterface.OnClickListener,
)

data class AlertDialogNegativeButton(
    val negativeButtonText: String,
    val negativeButtonListener: DialogInterface.OnClickListener,
)

fun showAlertDialog(context: Context, title: String, message: String, alertDialogButton: AlertDialogButton) {
    val alertDialogBuilder = AlertDialog.Builder(context)
    alertDialogBuilder.setTitle(title)
    alertDialogBuilder.setMessage(message)

    if (alertDialogButton.positiveButton != null) {
        val text = alertDialogButton.positiveButton.positiveButtonText
        val listener = alertDialogButton.positiveButton.positiveButtonListener
        alertDialogBuilder.setPositiveButton(text, listener)
    }

    if (alertDialogButton.negativeButton != null) {
        val text = alertDialogButton.negativeButton.negativeButtonText
        val listener = alertDialogButton.negativeButton.negativeButtonListener
        alertDialogBuilder.setNegativeButton(text, listener)
    }

    alertDialogBuilder.show()

}