package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.service.ProductApiService

class ProductDetailsActivity : AppCompatActivity() {
    // API SERVICE INSTANCE
    private val productApiService = ProductApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_details)

        // SET THE STATUS BAR BACKGROUND COLOR
        window.statusBarColor = getColor(R.color.black20)

        // SET UP THE TOOLBAR
        val toolbar: Toolbar = findViewById(R.id.pdToolbar)
        setSupportActionBar(toolbar)

        // ENABLE THE DEFAULT BACK BUTTON IN THE ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.productDetailsActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // SETUP VIEW DETAILS
        setupViewDetails()
    }

    // HANDLE THE DEFAULT BACK BUTTON PRESS
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // FUNCTION TO SETUP VIEW DETAILS
    private fun setupViewDetails() {
        // GET THE PRODUCT NAME FROM INTENT AND SET THE PRODUCT NAME
        supportActionBar?.title = ""
        val productNameTextView: TextView = findViewById(R.id.pdProductName)
        val productName = intent.getStringExtra("product_name")
        productNameTextView.text = productName

        // SET UP THE CART ICON CLICK LISTENER
        val cartIcon: ImageView = findViewById(R.id.pdCartIcon)
        cartIcon.setOnClickListener {
            val productId = intent.getStringExtra("product_id")
            if (productId != null) {
                handleCart(productId)
            } else {
                Toast.makeText(this, "Product ID is not available", Toast.LENGTH_SHORT).show()
            }
        }

        // GET THE PRODUCT IMAGE FROM INTENT AND SET THE PRODUCT IMAGE
        val productImageView: ImageView = findViewById(R.id.pdProductImage)
        val productImageResId = intent.getIntExtra("product_image", -1)
        productImageView.setImageResource(productImageResId)
    }

    // FUNCTION TO HANDLE CART LOGIC
    private fun handleCart(productId: String) {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
        return

        productApiService.cartProduct(productId) { response ->
            runOnUiThread {
                // DISPLAY FEEDBACK BASED ON RESPONSE
                if (response != null) {
                    Toast.makeText(this, "Product Added to Cart", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to Add Product to Cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}