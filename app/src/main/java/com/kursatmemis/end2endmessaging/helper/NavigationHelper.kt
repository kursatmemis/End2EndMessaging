package com.kursatmemis.end2endmessaging.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.kursatmemis.end2endmessaging.view.main.activity.MainActivity

fun goToMainActivityAndFinishIt(context: Context, activity: Activity) {
    activity.finish()
    val intentToMainActivity = Intent(context, MainActivity::class.java)
    context.startActivity(intentToMainActivity)
}


