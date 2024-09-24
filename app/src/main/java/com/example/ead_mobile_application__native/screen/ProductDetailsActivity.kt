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
import com.example.ead_mobile_application__native.service.CartApiService
import com.example.ead_mobile_application__native.service.ProductApiService

class ProductDetailsActivity : AppCompatActivity() {
    // API SERVICE INSTANCE
    private val productApiService = ProductApiService()
    private val cartApiService = CartApiService()

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

        // GET THE PRODUCT DESCRIPTION FROM INTENT AND SET THE PRODUCT DESCRIPTION
        val productDescriptionTextView: TextView = findViewById(R.id.pdProductDescription)
        val productDescription = intent.getStringExtra("product_description")
        productDescriptionTextView.text = productDescription

        // GET THE PRODUCT PRICE FROM INTENT AND SET THE PRODUCT PRICE
        val productPriceTextView: TextView = findViewById(R.id.pdProductPrice)
        val productPrice = intent.getDoubleExtra("product_price", 0.0)
        productPriceTextView.text = getString(R.string.price_format, productPrice)

        // GET THE PRODUCT CATEGORY FROM INTENT AND SET THE PRODUCT CATEGORY
        val productCategoryTextView: TextView = findViewById(R.id.pdProductCategory)
        val productCategory = intent.getStringExtra("product_category")
        productCategoryTextView.text = productCategory

        // GET THE PRODUCT STOCK QUANTITY FROM INTENT AND SET THE PRODUCT STOCK QUANTITY
        val productStockQuantityTextView: TextView = findViewById(R.id.pdProductStockQuantity)
        val productStockQuantity = intent.getIntExtra("product_stock_quantity", 0)
        productStockQuantityTextView.text = productStockQuantity.toString()

        // GET THE PRODUCT RATING FROM INTENT AND SET THE PRODUCT RATING
        val productRatingTextView: TextView = findViewById(R.id.pdProductRating)
        val productRating = intent.getFloatExtra("product_rating", 0.0f)
        productRatingTextView.text = productRating.toString()
    }

    // FUNCTION TO HANDLE CART LOGIC
    private fun handleCart(productId: String) {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
        return

        cartApiService.cartProduct(productId) { response ->
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