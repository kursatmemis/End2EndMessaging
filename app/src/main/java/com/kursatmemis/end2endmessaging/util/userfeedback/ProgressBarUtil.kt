package com.kursatmemis.end2endmessaging.util.userfeedback

import android.view.View
import android.widget.ProgressBar

fun showProgressBar(progressBar: ProgressBar) {
    progressBar.visibility = View.VISIBLE
}

fun closeProgressBar(progressBar: ProgressBar) {
    progressBar.visibility = View.INVISIBLE
}