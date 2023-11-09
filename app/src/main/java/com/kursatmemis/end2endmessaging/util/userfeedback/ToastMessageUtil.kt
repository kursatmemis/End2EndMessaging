package com.kursatmemis.end2endmessaging.util.userfeedback

import android.content.Context
import android.widget.Toast

fun showToastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, duration).show()
}








