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
    // INSTANCE OF THE CUSTOMER API SERVICE
    private val customerApiService = CustomerApiService()

    // TRACK PASSWORD VISIBILITY STATUS
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signUpActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INITIALIZE VIEWS
        val userNameEditText = findViewById<EditText>(R.id.suUserName)
        val emailEditText = findViewById<EditText>(R.id.suEmail)
        val passwordEditText = findViewById<EditText>(R.id.suPassword)
        val phoneNumberEditText = findViewById<EditText>(R.id.suPhoneNumber)
        val dateOfBirthEditText = findViewById<EditText>(R.id.suDateOfBirth)
        val genderSpinner = findViewById<Spinner>(R.id.suGender)

        val passwordToggle = findViewById<ImageView>(R.id.suPasswordToggle)
        val btnSignUp = findViewById<Button>(R.id.suBtnSignUp)
        val txtSignIn = findViewById<TextView>(R.id.suSignIn)

        // SET UP THE SPINNER FOR GENDER SELECTION
        setupGenderSpinner(genderSpinner)

        // SET UP PASSWORD VISIBILITY TOGGLE FUNCTIONALITY
        setupPasswordToggle(passwordEditText, passwordToggle)

        // SET UP DATE PICKER FOR DATE OF BIRTH EDIT-TEXT
        setupDatePicker(dateOfBirthEditText)

        // HANDLE SIGN-UP BUTTON CLICK
        btnSignUp.setOnClickListener {
            handleSignUp(userNameEditText, emailEditText, passwordEditText,
                phoneNumberEditText, dateOfBirthEditText, genderSpinner)
        }

        // HANDLE SIGN-IN TEXT CLICK
        txtSignIn.setOnClickListener {
            handleNavigation()
        }
    }

    // FUNCTION TO SET UP THE GENDER SPINNER
    private fun setupGenderSpinner(genderSpinner: Spinner) {
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = adapter
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

    // FUNCTION TO SET UP DATE-PICKER FOR DATE OF BIRTH
    private fun setupDatePicker(dateOfBirthEditText: EditText) {
        // DISABLE KEYBOARD INPUT FOR THE DATA EDIT-TEXT
        dateOfBirthEditText.inputType = InputType.TYPE_NULL
        dateOfBirthEditText.isFocusable = false

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

    // FUNCTION TO HANDLE SIGN-UP LOGIC
    private fun handleSignUp(
        userNameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText,
        phoneNumberEditText: EditText,
        dateOfBirthEditText: EditText,
        genderSpinner: Spinner
    ) {
        // RETRIEVE INPUT VALUES
        val userName = userNameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        val dateOfBirth = dateOfBirthEditText.text.toString()
        val gender = genderSpinner.selectedItem.toString()

        // CHECK IF ALL FIELDS ARE FILLED
        if (userName != "" && email != "" && password != "" &&
            phoneNumber != "" && dateOfBirth != "" && gender != "") {

            // CALL SIGN-UP METHOD FROM API SERVICE
            customerApiService.signUp(Customer(userName, email, password, phoneNumber, dateOfBirth, gender)) { response ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (response != null) {
                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            // ALERT USER TO FILL ALL FIELDS
            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show()
        }
    }

    // FUNCTION TO HANDLE SIGN-IN NAVIGATION
    private fun handleNavigation(){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}