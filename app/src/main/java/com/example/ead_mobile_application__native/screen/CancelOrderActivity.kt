package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.service.OrderApiService

class CancelOrderActivity : AppCompatActivity() {
    // API SERVICE INSTANCE
    private val orderApiService = OrderApiService(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cancel_order)

        // SET THE STATUS BAR BACKGROUND COLOR
        window.statusBarColor = getColor(R.color.black20)

        // SET UP THE TOOLBAR
        val toolbar: Toolbar = findViewById(R.id.coToolbar)
        setSupportActionBar(toolbar)

        // ENABLE THE DEFAULT BACK BUTTON IN THE ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cancelOrderActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INITIALIZE VIEWS
        val reasonEditText = findViewById<EditText>(R.id.ocReason)
        val btnCancel = findViewById<Button>(R.id.ocBtnCancel)

        // SETUP VIEW DETAILS
        val orderId = intent.getStringExtra("order_id")
        setupViewDetails(orderId)

        // HANDLE CANCEL BUTTON CLICK
        btnCancel.setOnClickListener {
            handleCancel(orderId, reasonEditText)
        }
    }

    // HANDLE THE DEFAULT BACK BUTTON PRESS
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // FUNCTION TO SETUP VIEW DETAILS
    private fun setupViewDetails(orderId: String?) {
        // GET THE ORDER ID FROM INTENT AND SET THE ORDER ID
        supportActionBar?.title = ""
        val orderIDTextView: TextView = findViewById(R.id.coOrderID)
        orderIDTextView.text = orderId
    }

    // FUNCTION TO HANDLE CANCEL LOGIC
    private fun handleCancel(orderId: String?, reasonEditText: EditText) {
        // RETRIEVE INPUT VALUES
        val reasonText = reasonEditText.text.toString()

        // CHECK IF BOTH FIELDS ARE FILLED
        if (reasonText.isEmpty()) {
            Toast.makeText(this, "Please Fill Reason Fields", Toast.LENGTH_SHORT).show()
        } else {
            // CALL RANKING METHOD FROM API SERVICE
            orderApiService.cancelOrder(orderId, reasonText) { status, message ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (status != null) {
                        when (status) {
                            200 -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, OrderActivity::class.java)).also { finish() }
                            }
                            401 -> {
                                Toast.makeText(this, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, OrderActivity::class.java)).also { finish() }
                            }
                            else -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Cancelling password failed: Please check your internet connection.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}