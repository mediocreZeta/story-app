package com.example.storyapp

import android.R.attr
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isSessionAvailable: Boolean = intent.getBooleanExtra("session", false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_main_activity) as NavHostFragment
        val navController = navHostFragment.navController

        val currentBackStack = navController.currentBackStackEntry?.destination?.id
        val loginFragmentId = R.id.loginFragment

        if (currentBackStack == loginFragmentId && isSessionAvailable) {
            navController.navigate(R.id.action_loginFragment_to_mainFragment)
        }
    }

}