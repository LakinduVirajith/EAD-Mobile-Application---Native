package com.example.ead_mobile_application__native

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.ead_mobile_application__native.screen.SignUpActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // Start SignUpActivity
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }
}