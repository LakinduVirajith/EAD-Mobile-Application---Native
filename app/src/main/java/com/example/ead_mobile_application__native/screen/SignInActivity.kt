package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.SignIn
import com.example.ead_mobile_application__native.service.AuthApiService

class SignInActivity : AppCompatActivity() {
    // INSTANCE OF THE AUTH API SERVICE
    private val authApiService = AuthApiService(this)

    // TRACK PASSWORD VISIBILITY STATUS
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        // SET STATUS BAR ICONS TO LIGHT (BLACK) IN DARK THEME
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signInActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INITIALIZE VIEWS
        val emailEditText = findViewById<EditText>(R.id.siEmail)
        val passwordEditText = findViewById<EditText>(R.id.siPassword)

        val passwordToggle = findViewById<ImageView>(R.id.siPasswordToggle)
        val btnSignIn = findViewById<Button>(R.id.siBtnSignIn)
        val txtSignUp = findViewById<TextView>(R.id.siSignUp)

        // SET UP PASSWORD VISIBILITY TOGGLE FUNCTIONALITY
        setupPasswordToggle(passwordEditText, passwordToggle)

        // HANDLE SIGN-IN BUTTON CLICK
        btnSignIn.setOnClickListener {
            handleSignIn(emailEditText, passwordEditText)
        }

        // HANDLE SIGN-UP TEXT CLICK
        txtSignUp.setOnClickListener {
            handleNavigation()
        }
    }

    // FUNCTION TO SET UP PASSWORD VISIBILITY TOGGLE
    private fun setupPasswordToggle(passwordEditText: EditText, passwordToggle: ImageView) {
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // SHOW PASSWORD
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_close_eye) // Closed eye icon
            } else {
                // HIDE PASSWORD
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_open_eye) // Open eye icon
            }
            // MOVE CURSOR TO THE END OF THE TEXT
            passwordEditText.setSelection(passwordEditText.text.length)
        }
    }

    // FUNCTION TO HANDLE SIGN-IN LOGIC
    private fun handleSignIn(emailEditText: EditText, passwordEditText: EditText) {
        // RETRIEVE INPUT VALUES
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // CHECK IF BOTH FIELDS ARE FILLED
        if (email != "" && password != "") {

            // CALL SIGN-IN METHOD FROM API SERVICE
            authApiService.signIn(SignIn(email, password)) { response ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (response != null) {
                        if(response.code == 200){
                            Toast.makeText(this, "${response.code}: Sign in successfully!.", Toast.LENGTH_SHORT).show()

//                            val intent = Intent(this, HomeActivity::class.java)
//                            startActivity(intent)
                        }else{
                            Toast.makeText(this, "${response.code}: Unauthorized. Please check your credentials and try again.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Sign Up Failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }

    // FUNCTION TO HANDLE SIGN-UP NAVIGATION
    private fun handleNavigation(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}