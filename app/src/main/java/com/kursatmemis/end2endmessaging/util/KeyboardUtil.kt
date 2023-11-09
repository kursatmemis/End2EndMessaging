package com.kursatmemis.end2endmessaging.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun closeKeyboard(activity: Activity, context: Context) {
    val view: View? = activity.currentFocus
    if (view != null) {
        val imm = ContextCompat.getSystemService(context, InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(view.windowToken, 0)
    }
}