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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.adapter.CartAdapter
import com.example.ead_mobile_application__native.adapter.OnCartChangeListener
import com.example.ead_mobile_application__native.helper.CartItemTouchHelper
import com.example.ead_mobile_application__native.model.Cart
import com.example.ead_mobile_application__native.service.CartApiService
import com.example.ead_mobile_application__native.service.CustomerApiService
import com.example.ead_mobile_application__native.service.OrderApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : AppCompatActivity(), OnCartChangeListener {
    // DECLARE VIEWS
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var layoutView : LinearLayout
    private lateinit var totalPriceText: TextView
    private lateinit var placeOrderButton: Button
    private lateinit var changeShippingButton: Button
    private lateinit var emptyCartText: TextView
    private lateinit var cartAdapter: CartAdapter

    // API SERVICE INSTANCE
    private val customerApiService = CustomerApiService(this)
    private val cartApiService = CartApiService(this)
    private val orderApiService = OrderApiService(this)

    // SAMPLE CART LIST
    private var cartList = mutableListOf<Cart>()

    // TO TRACE SHIPPING DETAILS
    private var hasShippingDetails = false

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
        changeShippingButton = findViewById(R.id.cBtnChangeShipping)
        emptyCartText = findViewById(R.id.cEmptyCartText)

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()

        // CHECK SHIPPING DETAILS AVAILABILITY
        checkShipping()

        // SETUP CART LIST AND ADAPTER
        setupCartList()

        // SET UP CLICK LISTENER FOR PLACE ORDER BUTTON
        placeOrderButton.setOnClickListener {
            handlePlaceOrder()
        }

        // SET UP CLICK LISTENER FOR CHANGE SHIPPING BUTTON
        changeShippingButton.setOnClickListener{
            val intent = Intent(this, ShippingDetailsActivity::class.java)
            startActivity(intent)
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

    // FUNCTION TO CHECK AVAILABILITY OF THE SHIPPING DETAILS
    private fun checkShipping(){
        customerApiService.checkShipping() { result ->
            runOnUiThread {
                result.onSuccess { isAvailable ->
                    hasShippingDetails = isAvailable
                }.onFailure { error ->
                    val errorMessage = error.message ?: "Failed to fetch shipping details."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // FUNCTION TO SETUP CART LIST AND ADAPTER
    private fun setupCartList() {
        cartAdapter = CartAdapter(cartList, this, this)
        cartRecyclerView.adapter = cartAdapter
        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        // SET UP ITEM TOUCH HELPER
        val itemTouchHelper = ItemTouchHelper(CartItemTouchHelper(cartAdapter))
        itemTouchHelper.attachToRecyclerView(cartRecyclerView)

        loadCart()
    }

    // IMPLEMENT THE CALLBACK METHOD
    override fun onCartChanged(cartItems: List<Cart>) {
        updateTotalPrice(cartItems)
    }

    private fun updateTotalPrice(cartItems: List<Cart>) {
        var calTotalPrice = 0.00
        cartItems.forEach { cartItem ->
            calTotalPrice += cartItem.price * cartItem.quantity
        }
        totalPriceText.text = getString(R.string.price_format, calTotalPrice)
    }

    // FUNCTION TO LOAD CART
    private fun loadCart(){
        cartApiService.cartProducts() { result ->
            runOnUiThread {
                // UPDATE CART LIST BASED ON RESPONSE
                if (result.isSuccess) {
                    val cartItems = result.getOrNull() ?: emptyList()
                    cartAdapter.updateItems(cartItems)

                    updateTotalPrice(cartItems)

                    // HANDLE CART VISIBILITY BASED ON ITEMS IN THE CART
                    if (cartItems.isEmpty()) {
                        layoutView.visibility = View.GONE
                        emptyCartText.visibility = View.VISIBLE
                    }else{
                        layoutView.visibility = View.VISIBLE
                        emptyCartText.visibility = View.GONE
                    }
                }
                // HANDLE ERROR (SHOW TOAST, LOG ERROR, ETC.)
                else {
                    layoutView.visibility = View.VISIBLE
                    emptyCartText.visibility = View.GONE

                    val error = result.exceptionOrNull()?.message ?: "Load failed. Please check your internet connection or try again."
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // FUNCTION TO HANDLE PLACE ORDER LOGIC
    private fun handlePlaceOrder(){
        if(!hasShippingDetails){
            val intent = Intent(this, ShippingDetailsActivity::class.java)
            startActivity(intent)
            return
        }
        if(cartList.isNotEmpty()){
            // GET TODAY'S DATE AND FORMAT IT TO "2000-10-31" STYLE
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // PLACE THE ORDER USING THE ORDER ITEM LIST
            orderApiService.placeOrder(todayDate) { status, message ->
                runOnUiThread {
                    if (status != null) {
                        when (status) {
                            200 -> {
                                Toast.makeText(this, "$status: $message", Toast.LENGTH_SHORT).show()

                                // NAVIGATION TO THE ORDERS
                                val intent = Intent(this, OrderActivity::class.java)
                                startActivity(intent)
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
        }
    }
}