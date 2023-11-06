package com.kursatmemis.end2endmessaging.view.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.view.main.activity.MainActivity
import com.kursatmemis.end2endmessaging.view.register.activity.RegisterActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            goToMainActivity()
        } else {
            goToRegisterAcitivty()
        }
        finish()
    }

    private fun goToMainActivity() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity)
    }

    private fun goToRegisterAcitivty() {
        val intentToRegisterActivity = Intent(this, RegisterActivity::class.java)
        startActivity(intentToRegisterActivity)
    }

}