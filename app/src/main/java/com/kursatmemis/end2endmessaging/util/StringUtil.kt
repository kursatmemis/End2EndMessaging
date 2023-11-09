package com.kursatmemis.end2endmessaging.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun areParamsEmpty(vararg params: String) : Boolean {
    for (param in params) {
        if (param.isEmpty()) {
            return true
        }
    }
    return false
}


