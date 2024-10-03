package com.example.ead_mobile_application__native.screen

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.ShippingDetails
import com.example.ead_mobile_application__native.service.CustomerApiService

class ShippingDetailsActivity : AppCompatActivity() {
    // API SERVICE INSTANCE
    private val customerApiService = CustomerApiService(this)

    // DECLARE VIEWS
    private lateinit var shippingButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shipping_details)

        // SET THE STATUS BAR BACKGROUND COLOR
        window.statusBarColor = getColor(R.color.black20)

        // SET UP THE TOOLBAR
        val toolbar: Toolbar = findViewById(R.id.sdToolbar)
        setSupportActionBar(toolbar)

        // ENABLE THE DEFAULT BACK BUTTON IN THE ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shippingDetailsActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FIND UI COMPONENTS BY ID
        shippingButton = findViewById(R.id.sdBtnShippingAddress)

        // CHECK SHIPPING DETAILS AVAILABILITY
        checkShipping()
    }

    // HANDLE THE DEFAULT BACK BUTTON PRESS
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // FUNCTION TO CHECK SHIPPING DETAILS AVAILABILITY
    private fun checkShipping() {
        customerApiService.checkShipping() { result ->
            runOnUiThread {
                result.onSuccess { isAvailable ->
                    if(isAvailable){
                        shippingButton.text = getString(R.string.change_shipping)
                    }else{
                        shippingButton.text = getString(R.string.add_shipping)
                    }

                    // SETUP VIEW DETAILS
                    setupViewDetails()
                }.onFailure { error ->
                    val errorMessage = error.message ?: "408: Shipping details fetching failed: Please check your internet connection."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // FUNCTION TO SETUP VIEW DETAILS
    private fun setupViewDetails() {
        val shippingAddressText = findViewById<TextView>(R.id.sdShippingAddress)
        val cityText = findViewById<TextView>(R.id.sdCity)
        val stateText = findViewById<TextView>(R.id.sdState)
        val postalCodeText = findViewById<TextView>(R.id.sdPostalCode)

        customerApiService.getShippingDetails { shipping ->
            runOnUiThread {
                if (shipping != null) {
                    shippingAddressText.text = shipping.address
                    cityText.text = shipping.city
                    stateText.text = shipping.state
                    postalCodeText.text = shipping.postalCode
                } else {
                    Toast.makeText(this, "Fetching account Failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        shippingButton.setOnClickListener{
            // RETRIEVE INPUT VALUES
            val address = shippingAddressText.text.toString()
            val city = cityText.text.toString()
            val state = stateText.text.toString()
            val postalCode = postalCodeText.text.toString()

            // CHECK IF ALL FIELDS ARE FILLED
            if (address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && postalCode.isNotEmpty()) {
                customerApiService.updateShippingDetails(ShippingDetails(address, city, state, postalCode)) { status, message ->
                    runOnUiThread {
                        // DISPLAY FEEDBACK BASED ON RESPONSE
                        if (status != null) {
                            when (status) {
                                200 -> {
                                    Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                401 -> {
                                    Toast.makeText(this, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}