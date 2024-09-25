package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.ProductDetails
import com.example.ead_mobile_application__native.service.CartApiService
import com.example.ead_mobile_application__native.service.ProductApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductDetailsActivity : AppCompatActivity() {
    // API SERVICE INSTANCE
    private val productApiService = ProductApiService()
    private val cartApiService = CartApiService()

    // DECLARE VARIABLES
    private lateinit var selectedSize: String
    private lateinit var selectedColor: String
    private var selectedQuantity: Int = 1

    // SAMPLE PRODUCT DETAILS
    private var productDetails: ProductDetails = ProductDetails(
        productId = "1",
        imageResId = R.drawable.product_1,
        name = "Casual Cotton T-Shirt",
        price = 19.99,
        discount = 4.00,
        size = listOf("S", "M", "L"),
        color = listOf("Black", "Green", "Orange"),
        description = "Elevate your casual wardrobe with our Casual Cotton T-Shirt. Made from 100% premium cotton, this t-shirt offers unmatched comfort and breathability. Its classic fit is perfect for everyday wear, whether you're out with friends or lounging at home. Available in a variety of colors, this versatile t-shirt pairs well with jeans, shorts, or joggers. Designed to withstand multiple washes while maintaining its shape and color, it's a must-have for every fashion enthusiast.",
        category = "Clothing",
        stockQuantity = 50,
        rating = 4.6f
    )

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

        // SETUP SPINNER VIEWS
        setupSpinner()

        // SETUP QUANTITY SELECTOR
        setupQuantitySelector()
    }

    // HANDLE THE DEFAULT BACK BUTTON PRESS
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // FUNCTION TO SETUP VIEW DETAILS
    private fun setupViewDetails() {
        val productId = intent.getIntExtra("product_id", 0)

//        // FETCH PRODUCT DETAILS
//        if(productId != null){
//            productApiService.fetchProductDetails(productId) { response ->
//                runOnUiThread {
//                    if (response != null) {
//                        val gson = Gson()
//                        val productType = object : TypeToken<ProductDetails>() {}.type
//                        productDetails = gson.fromJson(response, productType)
//                    }
//                }
//            }
//        }

        // SET UP THE CART ICON CLICK LISTENER
        val cartIcon: ImageView = findViewById(R.id.pdCartIcon)
        cartIcon.setOnClickListener {
            handleCart(productId)
        }

        // SET THE PRODUCT NAME
        supportActionBar?.title = ""
        val productNameTextView: TextView = findViewById(R.id.pdProductName)
        productNameTextView.text = productDetails.name

        // SET THE PRODUCT IMAGE
        val productImageView: ImageView = findViewById(R.id.pdProductImage)
        productImageView.setImageResource(productDetails.imageResId)

        // SET THE PRODUCT DESCRIPTION
        val productDescriptionTextView: TextView = findViewById(R.id.pdProductDescription)
        productDescriptionTextView.text = productDetails.description

        // SET THE PRODUCT PRICE
        val productPriceTextView: TextView = findViewById(R.id.pdProductPrice)
        productPriceTextView.text = getString(R.string.price_format, productDetails.price)
        productPriceTextView.paintFlags =  productPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        // SET THE DISCOUNT PRICE
        val productDiscountTextView: TextView = findViewById(R.id.pdDiscountPrice)
        val discountPrice = productDetails.price - (productDetails.price * productDetails.discount / 100)
        productDiscountTextView.text = getString(R.string.price_format, discountPrice)

        // SET THE PRODUCT CATEGORY
        val productCategoryTextView: TextView = findViewById(R.id.pdProductCategory)
        productCategoryTextView.text = productDetails.category

        // SET THE PRODUCT STOCK QUANTITY
        val productStockQuantityTextView: TextView = findViewById(R.id.pdProductStockQuantity)
        productStockQuantityTextView.text = productDetails.stockQuantity.toString()

        // SET THE PRODUCT RATING
        val productRatingTextView: TextView = findViewById(R.id.pdProductRating)
        productRatingTextView.text = productDetails.rating.toString()
    }

    // FUNCTION TO SETUP SPINNER VIEWS
    private fun setupSpinner() {
        // SET UP SIZE SPINNER
        val sizeSpinner: Spinner = findViewById(R.id.pdSizeSpinner)
        val sizeAdapter = ArrayAdapter(this, R.layout.spinner_item, productDetails.size)
        sizeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        sizeSpinner.adapter = sizeAdapter

        // SET UP COLOR SPINNER
        val colorSpinner: Spinner = findViewById(R.id.pdColorSpinner)
        val colorAdapter = ArrayAdapter(this, R.layout.spinner_item, productDetails.color)
        colorAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        colorSpinner.adapter = colorAdapter

        sizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedSize = productDetails.size[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedColor = productDetails.color[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    // FUNCTION TO SETUP QUANTITY SELECTOR
    private fun setupQuantitySelector(){
        val minusIcon: ImageView = findViewById(R.id.pdMinusIcon)
        val productQuantity: TextView = findViewById(R.id.pdProductQuantity)
        val plusIcon: ImageView = findViewById(R.id.pdPlusIcon)

        productQuantity.text = selectedQuantity.toString()

        plusIcon.setOnClickListener {
            // INCREMENT QUANTITY IF IT'S LESS THAN STOCK QUANTITY
            if (selectedQuantity < productDetails.stockQuantity) {
                selectedQuantity += 1
                productQuantity.text = selectedQuantity.toString()
            } else {
                Toast.makeText(this, "Can't Exceed Stock Quantity", Toast.LENGTH_SHORT).show()
            }
        }

        minusIcon.setOnClickListener {
            // DECREMENT QUANTITY IF IT'S GREATER THAN 1
            if (selectedQuantity > 1) {
                selectedQuantity -= 1
                productQuantity.text = selectedQuantity.toString()
            } else {
                Toast.makeText(this, "Quantity Can't be Less Than 1", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // FUNCTION TO HANDLE CART LOGIC
    private fun handleCart(productId: Int) {
        if(selectedSize.isEmpty()){
            Toast.makeText(this, "Please Select Size of the Product", Toast.LENGTH_SHORT).show()
        }else if(selectedColor.isEmpty()){
            Toast.makeText(this, "Please Select Color of the Product", Toast.LENGTH_SHORT).show()
        }else if(selectedQuantity.toString().isEmpty() || selectedQuantity < 1){
            Toast.makeText(this, "Invalid Quantity of the Product", Toast.LENGTH_SHORT).show()
        }else{
            cartApiService.addCart(productId, selectedSize, selectedColor, selectedQuantity) { response ->
                runOnUiThread {
                    // DISPLAY FEEDBACK BASED ON RESPONSE
                    if (response != null) {
                        Toast.makeText(this, "Product Added to Cart", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, CartActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Failed to Add Product to Cart", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // STOP SLIDING TO PREVENT MEMORY LEAKS
    override fun onDestroy() {
        selectedQuantity = 1
        super.onDestroy()
    }
}