package com.example.ead_mobile_application__native.screen

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.WindowInsetsController
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.CustomerDetails
import com.example.ead_mobile_application__native.service.AuthApiService
import com.example.ead_mobile_application__native.service.CustomerApiService
import com.example.ead_mobile_application__native.utils.DateUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar
import java.util.Locale

class AccountActivity : AppCompatActivity() {
    // INSTANCE OF THE API SERVICES
    private val customerApiService = CustomerApiService(this)
    private val authApiService = AuthApiService(this)

    // TRACK CURRENT PASSWORD VISIBILITY STATUS
    private var isCurrentPasswordVisible = false

    // TRACK NEW PASSWORD VISIBILITY STATUS
    private var isNewPasswordVisible = false

    // LATE INIT USER DETAILS
    private lateinit var customerDetails: CustomerDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.accountActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()
        // FETCH AND SETUP USER DETAILS
        fetchAndSetupUserDetails()
        // SETUP EMAIL CHANGE SECTIONS
        setupChangeEmailSection()

        // SETUP PASSWORD CHANGE SECTIONS
        setupChangePasswordSection()
        // SET UP CURRENT PASSWORD VISIBILITY TOGGLE FUNCTIONALITY
        setupCurrentPasswordToggle()
        // SET UP NEW PASSWORD VISIBILITY TOGGLE FUNCTIONALITY
        setupNewPasswordToggle()

        // SETUP ACCOUNT DETAILS CHANGE SECTIONS
        setupChangeDetailsSection()

        // SETUP DEACTIVATE ACCOUNT SECTIONS
        setupDeactivateAccountSection()
        // SETUP LOGOUT ACCOUNT SECTIONS
        setupLogoutAccountSection()
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

    // FUNCTION TO FETCH AND SETUP USER DETAILS
    private fun fetchAndSetupUserDetails() {
        customerApiService.getUserDetails { customer ->
            runOnUiThread {
                if (customer != null) {
                    customerDetails = CustomerDetails(customer.userName, customer.email,
                        customer.phoneNumber, customer.dateOfBirth, customer.gender)

                    val userNameEditText = findViewById<EditText>(R.id.pUserName)
                    val phoneNumberEditText = findViewById<EditText>(R.id.pPhoneNumber)
                    val dateOfBirthEditText = findViewById<EditText>(R.id.pDateOfBirth)
                    val genderSpinner = findViewById<Spinner>(R.id.pGender)

                    userNameEditText.hint = customer.userName
                    phoneNumberEditText.hint = customer.phoneNumber

                    // SET UP DATE PICKER FOR DATE OF BIRTH EDIT-TEXT
                    setupDatePicker(dateOfBirthEditText, customer.dateOfBirth)

                    // SET UP THE SPINNER FOR GENDER SELECTION
                    setupGenderSpinner(genderSpinner, customer.gender)
                } else {
                    Toast.makeText(this, "Fetching account Failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    authApiService.removeTokens()
                    startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                }
            }
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

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

        currentEmailSection.visibility = View.GONE
        newEmailSection.visibility = View.GONE
        changeEmailButton.visibility = View.GONE

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
            customerApiService.changeEmail(currentEmail, newEmail) { status, message ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (status != null) {
                        when (status) {
                            200 -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            401 -> {
                                Toast.makeText(this, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            else -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Change password failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

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
            customerApiService.changePassword(email, currentPassword, newPassword) { status, message ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (status != null) {
                        when (status) {
                            200 -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            401 -> {
                                Toast.makeText(this, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            else -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Change password failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    // SETUP CHANGE ACCOUNT DETAILS SECTION WITH TOGGLE AND ACTIONS
    private fun setupChangeDetailsSection() {
        // INITIALIZE EMAIL SECTION VIEWS
        val updateAccountDetailsSection = findViewById<LinearLayout>(R.id.pUpdateAccountDetailsSection)
        val userNameSection = findViewById<LinearLayout>(R.id.pUserNameSection)
        val phoneNumberSection = findViewById<LinearLayout>(R.id.pPhoneNumberSection)
        val dateOfBirthSection = findViewById<LinearLayout>(R.id.pDateOfBirthSection)
        val genderSection = findViewById<LinearLayout>(R.id.pGenderSection)

        val userNameEditText = findViewById<EditText>(R.id.pUserName)
        val phoneNumberEditText = findViewById<EditText>(R.id.pPhoneNumber)
        val dateOfBirthEditText = findViewById<EditText>(R.id.pDateOfBirth)
        val genderSpinner = findViewById<Spinner>(R.id.pGender)

        val toggleIcon = findViewById<ImageView>(R.id.pToggleIcon3)
        val updateDetailsButton = findViewById<Button>(R.id.pBtnUpdateAccountDetails)

        userNameSection.visibility = View.GONE
        phoneNumberSection.visibility = View.GONE
        dateOfBirthSection.visibility = View.GONE
        genderSection.visibility = View.GONE
        updateDetailsButton.visibility = View.GONE

        // TOGGLE PASSWORD SECTION VISIBILITY ON CLICK
        updateAccountDetailsSection.setOnClickListener {
            if (userNameSection.visibility == View.GONE) {
                userNameSection.visibility = View.VISIBLE
                phoneNumberSection.visibility = View.VISIBLE
                dateOfBirthSection.visibility = View.VISIBLE
                genderSection.visibility = View.VISIBLE
                updateDetailsButton.visibility = View.VISIBLE
                toggleIcon.setImageResource(R.drawable.ic_arrow_up) // CHANGE ICON WHEN EXPANDED
            } else {
                userNameSection.visibility = View.GONE
                phoneNumberSection.visibility = View.GONE
                dateOfBirthSection.visibility = View.GONE
                genderSection.visibility = View.GONE
                updateDetailsButton.visibility = View.GONE
                toggleIcon.setImageResource(R.drawable.ic_arrow_down) // CHANGE ICON WHEN COLLAPSED
            }
        }

        // HANDLE PASSWORD CHANGE BUTTON CLICK
        updateDetailsButton.setOnClickListener {
            handleUpdateAccountDetails(userNameEditText, phoneNumberEditText, dateOfBirthEditText, genderSpinner)
        }
    }

    // FUNCTION TO SET UP DATE-PICKER FOR DATE OF BIRTH
    private fun setupDatePicker(dateOfBirthEditText: EditText, dateOfBirth: String) {
        // DISABLE KEYBOARD INPUT FOR THE DATA EDIT-TEXT
        dateOfBirthEditText.inputType = InputType.TYPE_NULL
        dateOfBirthEditText.isFocusable = false

        // FORMAT THE DATE OF BIRTH TO "dd/MM/yyyy"
        val formattedDateOfBirth = DateUtils.formatDate(dateOfBirth, "yyyy-MM-dd", "dd/MM/yyyy")

        if (formattedDateOfBirth == null) {
            Toast.makeText(this, "Date format error", Toast.LENGTH_SHORT).show()
            return
        }

        dateOfBirthEditText.hint = formattedDateOfBirth.toString()

        // SET UP A CLICK LISTENER TO SHOW DATA-PICKER DIALOG
        dateOfBirthEditText.setOnClickListener {
            // CREATE A CALENDER INSTANCE FOR DATE-PICKER
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // CREATE DATE-PICKER DIALOG
            val datePickerDialog = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // FORMAT AND SET THE SELECTED DATE IN EDIT-TEXT
                    val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    dateOfBirthEditText.setText(formattedDate)
                }, year, month, day)

            datePickerDialog.show()
        }
    }

    // FUNCTION TO SET UP THE GENDER SPINNER
    private fun setupGenderSpinner(genderSpinner: Spinner, gender: String) {
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, genderOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        genderSpinner.adapter = adapter

        // FIND THE POSITION OF THE GENDER AND SET SELECTION
        val genderPosition = adapter.getPosition(gender)
        genderSpinner.setSelection(genderPosition)
    }

    // FUNCTION TO HANDLE UPDATE ACCOUNT LOGIC
    private fun handleUpdateAccountDetails(
        userNameEditText: EditText,
        phoneNumberEditText: EditText,
        dateOfBirthEditText: EditText,
        genderSpinner: Spinner) {

        // RETRIEVE INPUT VALUES
        val userName = userNameEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        val dateOfBirth = dateOfBirthEditText.text.toString()
        val gender = genderSpinner.selectedItem.toString()

        // CHECK IF ALL FIELDS ARE FILLED
        if (userName != "" && phoneNumber != "" && dateOfBirth != "" && gender != "") {

            // FORMAT THE DATE OF BIRTH TO "yyyy-MM-dd"
            val formattedDateOfBirth = DateUtils.formatDate(dateOfBirth, "dd/MM/yyyy", "yyyy-MM-dd")

            if (formattedDateOfBirth == null) {
                Toast.makeText(this, "Date format error", Toast.LENGTH_SHORT).show()
                return
            }

            // CALL CUSTOMER API SERVICE TO UPDATE ACCOUNT
            customerApiService.updateUserDetails(CustomerDetails(userName = userName, email = customerDetails.email, phoneNumber = phoneNumber, dateOfBirth = formattedDateOfBirth, gender = gender)) { status, message ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (status != null) {
                        when (status) {
                            200 -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            401 -> {
                                Toast.makeText(this, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            else -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Update account failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
                // ALERT USER TO FILL ALL FIELDS
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    // SETUP DEACTIVATE ACCOUNT SECTION WITH TOGGLE AND ACTIONS
    private fun setupDeactivateAccountSection(){
        // INITIALIZE EMAIL SECTION VIEWS
        val deactivateAccountSection = findViewById<LinearLayout>(R.id.pDeactivateAccountSection)
        val emailSection = findViewById<LinearLayout>(R.id.pDeactivateEmailSection)
        val emailEditText = findViewById<EditText>(R.id.pDeactivateEmail)
        val toggleIcon = findViewById<ImageView>(R.id.pToggleIcon4)
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
            customerApiService.deactivateAccount(email) { status, message ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (status != null) {
                        when (status) {
                            200 -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            401 -> {
                                Toast.makeText(this, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                                authApiService.removeTokens()
                                startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
                            }
                            else -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Deactivate account failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill Email Fields", Toast.LENGTH_SHORT).show()
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    // SETUP LOGOUT ACCOUNT SECTION
    private fun setupLogoutAccountSection(){
        val logoutAccountButton = findViewById<Button>(R.id.pBtnLogoutAccount)

        // HANDLE LOGOUT ACCOUNT BUTTON CLICK
        logoutAccountButton.setOnClickListener {
            Toast.makeText(this, "You have successfully logged out. See you next time!", Toast.LENGTH_SHORT).show()
            authApiService.removeTokens()
            startActivity(Intent(this, SignInActivity::class.java)).also { finish() }
        }
    }
}