package com.kursatmemis.end2endmessaging.view.main.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kursatmemis.end2endmessaging.R
import com.kursatmemis.end2endmessaging.databinding.ActivityMainBinding
import com.kursatmemis.end2endmessaging.view.register.activity.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*auth = Firebase.auth
        setSupportActionBar(binding.toolbar)*/

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)

    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                if (auth.currentUser != null) {
                    auth.signOut()
                    goToRegisterActivity()
                    finish()
                }
                return true
            }
        }

        return false
    }

    private fun goToRegisterActivity() {
        val intentToRegisterAcitivty = Intent(this, RegisterActivity::class.java)
        startActivity(intentToRegisterAcitivty)
    }*/

}