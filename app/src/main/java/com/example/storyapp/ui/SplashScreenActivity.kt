package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.MainActivity
import com.example.storyapp.other.SettingsPreference
import com.example.storyapp.other.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        val settingPreferences = SettingsPreference(applicationContext.dataStore)
        splashScreen.setKeepOnScreenCondition {
            true
        }

        lifecycleScope.launch {
            val isSessionAvailable = settingPreferences.getSessionAvailability().first()
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            intent.putExtra("session", isSessionAvailable)
            startActivity(intent)
            finish()
        }
    }
}