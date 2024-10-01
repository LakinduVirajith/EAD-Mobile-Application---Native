package com.example.ead_mobile_application__native

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.ead_mobile_application__native.screen.HomeActivity
import com.example.ead_mobile_application__native.screen.SignInActivity
import com.example.ead_mobile_application__native.screen.SignUpActivity
import com.example.ead_mobile_application__native.service.AuthApiService
import com.example.ead_mobile_application__native.utils.JwtUtils

class MainActivity : AppCompatActivity() {
    // INSTANCE OF THE AUTH API SERVICE
    private val authApiService = AuthApiService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // GET TOKEN VALUES
        val (token, refreshToken) = authApiService.getTokens()

        // CHECK TOKENS VALIDITY
        when {
            token.isNullOrEmpty() || refreshToken.isNullOrEmpty() -> {
                // CASE 1: NO TOKENS PRESENT, NAVIGATE TO SIGN-UP
                navigateToSignUp()
            }
            JwtUtils.isValidToken(token) -> {
                // CASE 2: TOKEN PRESENT AND VALID, NAVIGATE TO HOME
                navigateToHome()
            }
            !JwtUtils.isValidToken(token) -> {
                // CASE 3: TOKEN PRESENT BUT INVALID, TRY TO REFRESH
                if(JwtUtils.isValidToken(refreshToken)){
                    authApiService.signInWithRefreshToken(refreshToken) { status ->
                        if (status != null) {
                            if (status) {
                                // CASE 4: REFRESH TOKEN VALID, NAVIGATE TO HOME WITH NEW TOKEN
                                navigateToHome()
                            } else {
                                // CASE 5: BOTH TOKENS INVALID, NAVIGATE TO SIGN-IN
                                navigateToSignIn()
                            }
                        } else {
                            Toast.makeText(this, "Sign Up Failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    // CASE 5: BOTH TOKENS INVALID, NAVIGATE TO REGISTER
                    navigateToSignIn()
                }
            }
            else -> {
                // SHOULDN'T REACH HERE, BUT IN CASE, NAVIGATE TO REGISTER
                navigateToSignUp()
            }
        }
    }

    // FUNCTION TO NAVIGATE TO HOME ACTIVITY
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    // FUNCTION TO NAVIGATE TO SIGN-IN ACTIVITY
    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    // FUNCTION TO NAVIGATE TO SIGN-UP ACTIVITY
    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java) // Assuming SignInActivity is where registration is done
        startActivity(intent)
        finish()
    }
}