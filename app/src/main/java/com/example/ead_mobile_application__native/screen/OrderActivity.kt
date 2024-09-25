package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.adapter.OrderAdapter
import com.example.ead_mobile_application__native.model.Order
import com.example.ead_mobile_application__native.service.OrderApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrderActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var emptyOrderText: TextView

    // API SERVICE INSTANCE
    private val orderApiService = OrderApiService()

    // SAMPLE CART LIST
    private var orderList = listOf(
        Order(
            orderId = 1,
            productImageResId = R.drawable.product_2,
            orderDate = "2024/09/12",
            status = "Pending",
            totalOrderPrice = 245.65
        ),
        Order(
            orderId = 2,
            productImageResId = R.drawable.product_1,
            orderDate = "2024/09/14",
            status = "Processing",
            totalOrderPrice = 164.25
        ),
        Order(
            orderId = 3,
            productImageResId = R.drawable.product_6,
            orderDate = "2024/09/16",
            status = "Shipped",
            totalOrderPrice = 241.75
        ),
        Order(
            orderId = 4,
            productImageResId = R.drawable.product_2,
            orderDate = "2024/09/16",
            status = "Delivered",
            totalOrderPrice = 241.75
        ),
        Order(
            orderId = 5,
            productImageResId = R.drawable.product_4,
            orderDate = "2024/09/16",
            status = "Completed",
            totalOrderPrice = 545.25
        ),
        Order(
            orderId = 6,
            productImageResId = R.drawable.product_3,
            orderDate = "2024/09/19",
            status = "Cancelled",
            totalOrderPrice = 80.50
        ),
        Order(
            orderId = 7,
            productImageResId = R.drawable.product_5,
            orderDate = "2024/09/20",
            status = "Refunded",
            totalOrderPrice = 150.50
        ),
        Order(
            orderId = 8,
            productImageResId = R.drawable.product_6,
            orderDate = "2024/09/24",
            status = "Returned",
            totalOrderPrice = 425.50
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orderActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FIND UI COMPONENTS BY ID
        orderRecyclerView = findViewById(R.id.oRecyclerView)
        emptyOrderText = findViewById(R.id.oEmptyOrderText)

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()

        // SETUP ORDER LIST AND ADAPTER
        setupOrderList()
    }

    // SETUP BOTTOM NAVIGATION VIEW AND ITS ITEM SELECTION
    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nvOrder

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nvHome -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
                R.id.nvOrder -> true
                R.id.nvCart -> {
                    startActivity(Intent(applicationContext, CartActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
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

    // FUNCTION TO SETUP ORDER LIST AND ADAPTER
    private fun setupOrderList() {
//        orderApiService.fetchOrders() { response ->
//            runOnUiThread {
//                // UPDATE ORDER LIST BASED ON RESPONSE
//                if (response != null) {
//                    val gson = Gson()
//                    val productType = object : TypeToken<List<Order>>() {}.type
//                    updateCartList(gson.fromJson(response, productType))
//                }
//            }
//        }

        // INITIALIZE THE ADEPTER WITH AND ORDER LIST
        orderAdapter = OrderAdapter(orderList)
        orderRecyclerView.adapter = orderAdapter
        orderRecyclerView.layoutManager = LinearLayoutManager(this)



        // HANDLE ORDER VISIBILITY BASED ON ITEMS IN THE ORDER
        if (orderList.isEmpty()) {
            orderRecyclerView.visibility = View.GONE
            emptyOrderText.visibility = View.VISIBLE
        }else{
            orderRecyclerView.visibility = View.VISIBLE
            emptyOrderText.visibility = View.GONE
        }
    }

    // FUNCTION TO UPDATE ORDER LIST
    private fun updateCartList(orders: List<Order>) {
        orderList = orders
        orderAdapter.updateItems(orders)
    }
}