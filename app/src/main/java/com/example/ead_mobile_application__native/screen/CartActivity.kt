package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.adapter.CartAdapter
import com.example.ead_mobile_application__native.model.Cart
import com.example.ead_mobile_application__native.model.OrderItem
import com.example.ead_mobile_application__native.service.CartApiService
import com.example.ead_mobile_application__native.service.OrderApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var layoutView : LinearLayout
    private lateinit var totalPriceText: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var emptyCartText: TextView
    private lateinit var cartAdapter: CartAdapter

    // API SERVICE INSTANCE
    private val cartApiService = CartApiService()
    private val orderApiService = OrderApiService()

    // SAMPLE CART LIST
    private var cartList = listOf(
        Cart(
            id = "1",
            name = "Casual Cotton T-Shirt",
            price = 19.99,
            discount = 4.00,
            imageResId = R.drawable.product_1,
            quantity = 1
        ),
        Cart(
            id = "4",
            name = "Classic Chino Pants",
            price = 34.99,
            discount = 7.00,
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
        cartRecyclerView = findViewById(R.id.cRecyclerView)
        layoutView = findViewById(R.id.cLayoutView)
        totalPriceText = findViewById(R.id.cTotalPriceText)
        placeOrderButton = findViewById(R.id.cBtnPlaceOrder)
        emptyCartText = findViewById(R.id.cEmptyCartText)

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()

        // SETUP CART LIST AND ADAPTER
        setupCartList()

        // SET UP CLICK LISTENER FOR PLACE ORDER BUTTON
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

        // CALCULATE TOTAL PRICE FROM CART LIST
        if (cartList.isNotEmpty()) {
            var calTotalPrice = 0.00
            cartList.map { cartItem ->
                calTotalPrice += cartItem.price * cartItem.quantity
            }
            totalPriceText.text = getString(R.string.price_format, calTotalPrice)
        }

        // HANDLE CART VISIBILITY BASED ON ITEMS IN THE CART
        if (cartList.isEmpty()) {
            layoutView.visibility = View.GONE
            emptyCartText.visibility = View.VISIBLE
        }else{
            layoutView.visibility = View.VISIBLE
            emptyCartText.visibility = View.GONE
        }
    }

    // FUNCTION TO UPDATE CART LIST
    private fun updateCartList(items: List<Cart>) {
        cartList = items
        cartAdapter.updateItems(items)
    }

    // FUNCTION TO HANDLE PLACE ORDER LOGIC
    private fun handlePlaceOrder(){
        if(cartList.isNotEmpty()){
            // CONVERT CART LIST TO ORDER ITEM LIST
            val orderItems = cartList.map { cartItem ->
                OrderItem(
                    id = cartItem.id,
                    quantity = cartItem.quantity
                )
            }

            // PLACE THE ORDER USING THE ORDER ITEM LIST
            orderApiService.placeOrder(orderItems) { response ->
                if (response != null) {
                    Toast.makeText(this, "Order Placed Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, OrderActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Order Placed Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}