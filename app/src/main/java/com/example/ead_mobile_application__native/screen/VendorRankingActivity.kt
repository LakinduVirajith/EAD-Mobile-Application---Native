package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.service.VendorApiService

class VendorRankingActivity : AppCompatActivity() {
    // API SERVICE INSTANCE
    private val vendorApiService = VendorApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vendor_ranking)

        // SET THE STATUS BAR BACKGROUND COLOR
        window.statusBarColor = getColor(R.color.black20)

        // SET UP THE TOOLBAR
        val toolbar: Toolbar = findViewById(R.id.vrToolbar)
        setSupportActionBar(toolbar)

        // ENABLE THE DEFAULT BACK BUTTON IN THE ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vendorRankingActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INITIALIZE VIEWS
        val commentEditText = findViewById<EditText>(R.id.vrComment)
        val ratingBar = findViewById<RatingBar>(R.id.vrRatingBar)
        val btnSend = findViewById<Button>(R.id.vrBtnSend)
        val btnOrderDetails = findViewById<Button>(R.id.vrBtnOrderDetails)

        // SETUP VIEW DETAILS
        val orderIdNumber = intent.getIntExtra("order_id", 0)
        setupViewDetails(orderIdNumber)

        // HANDLE SEND BUTTON CLICK
        btnSend.setOnClickListener {
            handleSend(commentEditText, ratingBar)
        }

        // HANDLE NAVIGATION BUTTON CLICK
        btnOrderDetails.setOnClickListener {
            handleNavigation(orderIdNumber)
        }
    }

    // HANDLE THE DEFAULT BACK BUTTON PRESS
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // FUNCTION TO SETUP VIEW DETAILS
    private fun setupViewDetails(orderId: Int) {
        // GET THE ORDER ID FROM INTENT AND SET THE ORDER ID
        supportActionBar?.title = ""
        val orderIDTextView: TextView = findViewById(R.id.vrOrderID)
        orderIDTextView.text = String.format(getString(R.string.order_id_format), orderId)
    }

    // FUNCTION TO HANDLE SEND LOGIC
    private fun handleSend(commentEditText: EditText, ratingBar: RatingBar) {
        // RETRIEVE INPUT VALUES
        val comment = commentEditText.text.toString()
        val rating = ratingBar.rating.toString()

        // CHECK IF BOTH FIELDS ARE FILLED
        if (comment.isEmpty()) {
            Toast.makeText(this, "Please Fill Comment Fields", Toast.LENGTH_SHORT).show()
        } else if(rating.isEmpty()){
            Toast.makeText(this, "Please Select Ratings", Toast.LENGTH_SHORT).show()
        }else {
            // CALL RANKING METHOD FROM API SERVICE
            vendorApiService.addRanking(comment, rating.toInt()) { response ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (response != null) {
                        Toast.makeText(this, "Send Successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, OrderActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Send Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // FUNCTION TO HANDLE NAVIGATION
    private fun handleNavigation(orderId: Int){
        val intent = Intent(this, OrderDetailsActivity::class.java).apply {
            putExtra("order_id", orderId)
        }
        startActivity(intent)
    }
}