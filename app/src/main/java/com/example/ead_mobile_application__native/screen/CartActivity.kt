package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.adapter.CartAdapter
import com.example.ead_mobile_application__native.model.Cart
import com.example.ead_mobile_application__native.service.CartApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var placeOrderButton: Button
    private lateinit var emptyCartText: TextView
    private lateinit var cartAdapter: CartAdapter

    // API SERVICE INSTANCE
    private val cartApiService = CartApiService()

    // SAMPLE PRODUCT LIST
    private var cartList = listOf(
        Cart(
            id = "00001",
            name = "Product 1",
            price = 10.00,
            imageResId = R.drawable.product_1,
            quantity = 1
        ),
        Cart(
            id = "00004",
            name = "Product 4",
            price = 25.00,
            imageResId = R.drawable.product_4,
            quantity = 2
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cartActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FIND UI COMPONENTS BY ID
        cartRecyclerView = findViewById(R.id.cCartRecyclerView)
        placeOrderButton = findViewById(R.id.cBtnPlaceOrder)
        emptyCartText = findViewById(R.id.cEmptyCartText)

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()

        // SETUP CART LIST AND ADAPTER
        setupCartList()

        // HANDLE PLACE ORDER BUTTON CLICK
        placeOrderButton.setOnClickListener {
            handlePlaceOrder()
        }
    }

    // SETUP BOTTOM NAVIGATION VIEW AND ITS ITEM SELECTION
    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nvCart

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
                R.id.nvCart -> true
                R.id.nvAccount -> {
                    startActivity(Intent(applicationContext, AccountActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                else -> false
            }
        }
    }

    // FUNCTION TO SETUP CART LIST AND ADAPTER
    private fun setupCartList() {
//        cartApiService.cartProducts() { response ->
//            runOnUiThread {
//                // UPDATE CART LIST BASED ON RESPONSE
//                if (response != null) {
//                    val gson = Gson()
//                    val productType = object : TypeToken<List<Product>>() {}.type
//                    updateCartList(gson.fromJson(response, productType))
//                }
//            }
//        }

        // INITIALIZE THE ADEPTER WITH AND CART LIST
        cartAdapter = CartAdapter(cartList)
        cartRecyclerView.adapter = cartAdapter
        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        if (cartList.isEmpty()) {
            emptyCartText.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
            placeOrderButton.visibility = View.GONE
        }else{
            emptyCartText.visibility = View.GONE
            cartRecyclerView.visibility = View.VISIBLE
            placeOrderButton.visibility = View.VISIBLE
        }
    }

    // FUNCTION TO UPDATE CART LIST
    private fun updateCartList(items: List<Cart>) {
        cartList = items
        cartAdapter.updateItems(items)
    }

    // FUNCTION TO HANDLE PLACE ORDER LOGIC
    private fun handlePlaceOrder(){

    }
}