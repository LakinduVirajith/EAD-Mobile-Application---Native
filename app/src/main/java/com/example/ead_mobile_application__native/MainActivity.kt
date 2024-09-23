package com.example.ead_mobile_application__native

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.ead_mobile_application__native.screen.SignInActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // START SIGN-IN ACTIVITY
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}