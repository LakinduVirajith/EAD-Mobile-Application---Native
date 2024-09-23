package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.service.CustomerApiService
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountActivity : AppCompatActivity() {
    // INSTANCE OF THE CUSTOMER API SERVICE
    private val customerApiService = CustomerApiService()

    // TRACK CURRENT PASSWORD VISIBILITY STATUS
    private var isCurrentPasswordVisible = false

    // TRACK NEW PASSWORD VISIBILITY STATUS
    private var isNewPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.accountActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()
        // SETUP EMAIL CHANGE SECTIONS
        setupChangeEmailSection()

        // SETUP PASSWORD CHANGE SECTIONS
        setupChangePasswordSection()
        // SET UP CURRENT PASSWORD VISIBILITY TOGGLE FUNCTIONALITY
        setupCurrentPasswordToggle()
        // SET UP NEW PASSWORD VISIBILITY TOGGLE FUNCTIONALITY
        setupNewPasswordToggle()

        // SETUP DEACTIVATE ACCOUNT SECTIONS
        setupDeactivateAccountSection()
    }

    // SETUP BOTTOM NAVIGATION VIEW AND ITS ITEM SELECTION
    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nvAccount

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nvHome -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.nvOrder -> {
                    startActivity(Intent(applicationContext, OrderActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.nvCart -> {
                    startActivity(Intent(applicationContext, CartActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.nvAccount -> true
                else -> false
            }
        }
    }

/*  ------------------------------------------------------------------------------------------------    */

    // SETUP CHANGE EMAIL SECTION WITH TOGGLE AND ACTIONS
    private fun setupChangeEmailSection() {
        // INITIALIZE EMAIL SECTION VIEWS
        val changeEmailSection = findViewById<LinearLayout>(R.id.pChangeEmailSection)
        val currentEmailSection = findViewById<LinearLayout>(R.id.pCurrentEmailSection)
        val newEmailSection = findViewById<LinearLayout>(R.id.pNewEmailSection)
        val currentEmailEditText = findViewById<EditText>(R.id.pCurrentEmail)
        val newEmailEditText = findViewById<EditText>(R.id.pNewEmail)
        val toggleIcon = findViewById<ImageView>(R.id.pToggleIcon1)
        val changeEmailButton = findViewById<Button>(R.id.pBtnChangeEmail)

        // TOGGLE EMAIL SECTION VISIBILITY ON CLICK
        changeEmailSection.setOnClickListener {
            if (currentEmailSection.visibility == View.GONE) {
                currentEmailSection.visibility = View.VISIBLE
                newEmailSection.visibility = View.VISIBLE
                changeEmailButton.visibility = View.VISIBLE
                toggleIcon.setImageResource(R.drawable.ic_arrow_up) // CHANGE ICON WHEN EXPANDED
            } else {
                currentEmailSection.visibility = View.GONE
                newEmailSection.visibility = View.GONE
                changeEmailButton.visibility = View.GONE
                toggleIcon.setImageResource(R.drawable.ic_arrow_down) // CHANGE ICON WHEN COLLAPSED
            }
        }

        // HANDLE EMAIL CHANGE BUTTON CLICK
        changeEmailButton.setOnClickListener {
            handleEmailChange(currentEmailEditText, newEmailEditText)
        }
    }

    // FUNCTION TO HANDLE EMAIL CHANGE LOGIC
    private fun handleEmailChange(currentEmailEditText: EditText, newEmailEditText: EditText) {
        // RETRIEVE EMAIL INPUT VALUES
        val currentEmail = currentEmailEditText.text.toString()
        val newEmail = newEmailEditText.text.toString()

        // CHECK IF BOTH FIELDS ARE FILLED
        if (currentEmail != "" && newEmail != "") {

            // CALL CUSTOMER API SERVICE TO CHANGE EMAIL
            customerApiService.changeEmail(currentEmail, newEmail) { response ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (response != null) {
                        Toast.makeText(this, "Change Email Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Change Email Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }

/*  ------------------------------------------------------------------------------------------------    */

    // SETUP CHANGE PASSWORD SECTION WITH TOGGLE AND ACTIONS
    private fun setupChangePasswordSection() {
        // INITIALIZE EMAIL SECTION VIEWS
        val changePasswordSection = findViewById<LinearLayout>(R.id.pChangePasswordSection)
        val emailSection = findViewById<LinearLayout>(R.id.pEmailSection)
        val currentPasswordSection = findViewById<LinearLayout>(R.id.pCurrentPasswordSection)
        val newPasswordSection = findViewById<LinearLayout>(R.id.pNewPasswordSection)

        val emailEditText = findViewById<EditText>(R.id.pEmail)
        val currentPasswordEditText = findViewById<EditText>(R.id.pCurrentPassword)
        val newPasswordEditText = findViewById<EditText>(R.id.pNewPassword)

        val toggleIcon = findViewById<ImageView>(R.id.pToggleIcon2)
        val changePasswordButton = findViewById<Button>(R.id.pBtnChangePassword)

        emailSection.visibility = View.GONE
        currentPasswordSection.visibility = View.GONE
        newPasswordSection.visibility = View.GONE
        changePasswordButton.visibility = View.GONE

        // TOGGLE PASSWORD SECTION VISIBILITY ON CLICK
        changePasswordSection.setOnClickListener {
            if (emailSection.visibility == View.GONE) {
                emailSection.visibility = View.VISIBLE
                currentPasswordSection.visibility = View.VISIBLE
                newPasswordSection.visibility = View.VISIBLE
                changePasswordButton.visibility = View.VISIBLE
                toggleIcon.setImageResource(R.drawable.ic_arrow_up) // CHANGE ICON WHEN EXPANDED
            } else {
                emailSection.visibility = View.GONE
                currentPasswordSection.visibility = View.GONE
                newPasswordSection.visibility = View.GONE
                changePasswordButton.visibility = View.GONE
                toggleIcon.setImageResource(R.drawable.ic_arrow_down) // CHANGE ICON WHEN COLLAPSED
            }
        }

        // HANDLE PASSWORD CHANGE BUTTON CLICK
        changePasswordButton.setOnClickListener {
            handlePasswordChange(emailEditText, currentPasswordEditText, newPasswordEditText)
        }
    }

    // FUNCTION TO SET UP CURRENT PASSWORD VISIBILITY TOGGLE
    private fun setupCurrentPasswordToggle() {
        val passwordEditText = findViewById<EditText>(R.id.pCurrentPassword)
        val passwordToggle = findViewById<ImageView>(R.id.pCurrentPasswordToggle)

        passwordToggle.setOnClickListener {
            isCurrentPasswordVisible = !isCurrentPasswordVisible
            if (isCurrentPasswordVisible) {
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

    // FUNCTION TO SET UP NEW PASSWORD VISIBILITY TOGGLE
    private fun setupNewPasswordToggle() {
        val passwordEditText = findViewById<EditText>(R.id.pNewPassword)
        val passwordToggle = findViewById<ImageView>(R.id.pNewPasswordToggle)

        passwordToggle.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible
            if (isNewPasswordVisible) {
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

    // FUNCTION TO HANDLE EMAIL CHANGE LOGIC
    private fun handlePasswordChange(emailEditText : EditText, currentPasswordEditText : EditText, newPasswordEditText : EditText) {
        // RETRIEVE EMAIL INPUT VALUES
        val email = emailEditText.text.toString()
        val currentPassword = currentPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()

        // CHECK IF BOTH FIELDS ARE FILLED
        if (email != "" && currentPassword != "" && newPassword != "") {

            // CALL CUSTOMER API SERVICE TO CHANGE PASSWORD
            customerApiService.changePassword(email, currentPassword, newPassword) { response ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (response != null) {
                        Toast.makeText(this, "Change Password Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Change Password Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }

/*  ------------------------------------------------------------------------------------------------    */

    // SETUP DEACTIVATE ACCOUNT SECTION WITH TOGGLE AND ACTIONS
    private fun setupDeactivateAccountSection(){
        // INITIALIZE EMAIL SECTION VIEWS
        val deactivateAccountSection = findViewById<LinearLayout>(R.id.pDeactivateAccountSection)
        val emailSection = findViewById<LinearLayout>(R.id.pDeactivateEmailSection)
        val emailEditText = findViewById<EditText>(R.id.pDeactivateEmail)
        val toggleIcon = findViewById<ImageView>(R.id.pToggleIcon3)
        val deactivateAccountButton = findViewById<Button>(R.id.pBtnDeactivateAccount)

        emailSection.visibility = View.GONE
        deactivateAccountButton.visibility = View.GONE

        // TOGGLE EMAIL SECTION VISIBILITY ON CLICK
        deactivateAccountSection.setOnClickListener {
            if (emailSection.visibility == View.GONE) {
                emailSection.visibility = View.VISIBLE
                deactivateAccountButton.visibility = View.VISIBLE
                toggleIcon.setImageResource(R.drawable.ic_arrow_up) // CHANGE ICON WHEN EXPANDED
            } else {
                emailSection.visibility = View.GONE
                deactivateAccountButton.visibility = View.GONE
                toggleIcon.setImageResource(R.drawable.ic_arrow_down) // CHANGE ICON WHEN COLLAPSED
            }
        }

        // HANDLE DEACTIVATE ACCOUNT BUTTON CLICK
        deactivateAccountButton.setOnClickListener {
            handleDeactivateAccount(emailEditText)
        }
    }

    // FUNCTION TO HANDLE DEACTIVATE ACCOUNT LOGIC
    private fun handleDeactivateAccount(emailEditText : EditText) {
        // RETRIEVE EMAIL INPUT VALUES
        val email = emailEditText.text.toString()

        // CHECK IF EMAIL FIELD ARE FILLED
        if (email != "") {

            // CALL CUSTOMER API SERVICE TO DEACTIVATE ACCOUNT
            customerApiService.deactivateAccount(email) { response ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (response != null) {
                        Toast.makeText(this, "Deactivate Account Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Deactivate Account Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill Email Fields", Toast.LENGTH_SHORT).show()
        }
    }
}