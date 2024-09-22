package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
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

    // SETUP CHANGE PASSWORD SECTION WITH TOGGLE AND ACTIONS
    private fun setupChangePasswordSection() {
        // INITIALIZE EMAIL SECTION VIEWS
        val changePasswordSection = findViewById<LinearLayout>(R.id.pChangePasswordSection)
        val emailSection = findViewById<LinearLayout>(R.id.pEmailSection)
        val emailEditText = findViewById<EditText>(R.id.pEmail)
        val toggleIcon = findViewById<ImageView>(R.id.pToggleIcon2)
        val changePasswordButton = findViewById<Button>(R.id.pBtnChangePassword)

        emailSection.visibility = View.GONE
        changePasswordButton.visibility = View.GONE

        // TOGGLE PASSWORD SECTION VISIBILITY ON CLICK
        changePasswordSection.setOnClickListener {
            if (emailSection.visibility == View.GONE) {
                emailSection.visibility = View.VISIBLE
                changePasswordButton.visibility = View.VISIBLE
                toggleIcon.setImageResource(R.drawable.ic_arrow_up) // CHANGE ICON WHEN EXPANDED
            } else {
                emailSection.visibility = View.GONE
                changePasswordButton.visibility = View.GONE
                toggleIcon.setImageResource(R.drawable.ic_arrow_down) // CHANGE ICON WHEN COLLAPSED
            }
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
                        Toast.makeText(this, "Change Email successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Change Email failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

}