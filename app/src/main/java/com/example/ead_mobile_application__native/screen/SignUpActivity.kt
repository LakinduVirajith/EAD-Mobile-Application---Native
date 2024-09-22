package com.example.ead_mobile_application__native.screen

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
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
import com.example.ead_mobile_application__native.service.CustomerApiService
import java.util.Calendar
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    // Instance of the customer API service
    private val customerApiService = CustomerApiService()

    // Track password visibility status
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUpActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        val userNameEditText = findViewById<EditText>(R.id.suUserName)
        val emailEditText = findViewById<EditText>(R.id.suEmail)
        val passwordEditText = findViewById<EditText>(R.id.suPassword)
        val phoneNumberEditText = findViewById<EditText>(R.id.suPhoneNumber)
        val dateOfBirthEditText = findViewById<EditText>(R.id.suDateOfBirth)
        val genderSpinner = findViewById<Spinner>(R.id.suGender)

        val passwordToggle = findViewById<ImageView>(R.id.suPasswordToggle)
        val btnSignUp = findViewById<Button>(R.id.suBtnSignUp)
        val txtSignIn = findViewById<TextView>(R.id.suSignIn)

        // Set up the spinner for gender selection
        setupGenderSpinner(genderSpinner)

        // Set up password visibility toggle functionality
        setupPasswordToggle(passwordEditText, passwordToggle)

        // Set up date picker for Date of Birth EditText
        setupDatePicker(dateOfBirthEditText)

        // Handle sign-up button click
        btnSignUp.setOnClickListener {
            handleSignUp(userNameEditText, emailEditText, passwordEditText,
                phoneNumberEditText, dateOfBirthEditText, genderSpinner)
        }

        // Handle sign-in text click
        txtSignIn.setOnClickListener {
            handleNavigation()
        }
    }

    // Function to set up the gender spinner
    private fun setupGenderSpinner(genderSpinner: Spinner) {
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter
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

    // Function to set up DatePicker for date of birth
    private fun setupDatePicker(dateOfBirthEditText: EditText) {
        // Disable keyboard input for the date EditText
        dateOfBirthEditText.inputType = InputType.TYPE_NULL
        dateOfBirthEditText.isFocusable = false

        // Set up a click listener to show DatePickerDialog
        dateOfBirthEditText.setOnClickListener {
            // Create a calendar instance for DatePicker
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create DatePickerDialog
            val datePickerDialog = DatePickerDialog(this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format and set the selected date in EditText
                    val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    dateOfBirthEditText.setText(formattedDate)
                }, year, month, day)

            datePickerDialog.show()
        }
    }

    // Function to handle sign-up logic
    private fun handleSignUp(
        userNameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText,
        phoneNumberEditText: EditText,
        dateOfBirthEditText: EditText,
        genderSpinner: Spinner
    ) {
        // Retrieve input values
        val userName = userNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        val dateOfBirth = dateOfBirthEditText.text.toString()
        val gender = genderSpinner.selectedItem.toString()

        // Check if all fields are filled
        if (userName != "" && email != "" && password != "" &&
            phoneNumber != "" && dateOfBirth != "" && gender != "") {

            // Call sign-up method from API service
            customerApiService.signUp(Customer(userName, email, password, phoneNumber, dateOfBirth, gender)) { response ->
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

    // Function to handle sign-in navigation
    private fun handleNavigation(){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}