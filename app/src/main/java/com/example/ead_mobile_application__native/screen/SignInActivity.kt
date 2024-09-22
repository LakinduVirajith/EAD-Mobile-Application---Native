package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.Customer
import com.example.ead_mobile_application__native.model.SignIn
import com.example.ead_mobile_application__native.service.CustomerApiService

class SignInActivity : AppCompatActivity() {
    // Instance of the customer API service
    private val customerApiService = CustomerApiService()

    // Track password visibility status
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signInActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        val emailEditText = findViewById<EditText>(R.id.siEmail)
        val passwordEditText = findViewById<EditText>(R.id.siPassword)

        val passwordToggle = findViewById<ImageView>(R.id.siPasswordToggle)
        val btnSignIn = findViewById<Button>(R.id.siBtnSignIn)
        val txtSignUp = findViewById<TextView>(R.id.siSignUp)

        // Set up password visibility toggle functionality
        setupPasswordToggle(passwordEditText, passwordToggle)

        // Handle sign-up button click
        btnSignIn.setOnClickListener {
            handleSignIn(emailEditText, passwordEditText)
        }

        // Handle sign-up text click
        txtSignUp.setOnClickListener {
            handleNavigation()
        }
    }

    // Function to set up password visibility toggle
    private fun setupPasswordToggle(passwordEditText: EditText, passwordToggle: ImageView) {
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Show password
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24) // Closed eye icon
            } else {
                // Hide password
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_baseline_visibility_24) // Open eye icon
            }
            // Move cursor to the end of the text
            passwordEditText.setSelection(passwordEditText.text.length)
        }
    }

    // Function to handle sign-in logic
    private fun handleSignIn(
        emailEditText: EditText,
        passwordEditText: EditText,
    ) {
        // Retrieve input values
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Check if all fields are filled
        if (email != "" && password != "") {
            // Call sign-in method from API service
            customerApiService.signIn(SignIn(email, password)) { response ->
                runOnUiThread {
                    // Show feedback based on response
                    if (response != null) {
                        Toast.makeText(this, "Sign Up successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Sign Up failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // Alert user to fill all fields
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to handle sign-up navigation
    private fun handleNavigation(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}