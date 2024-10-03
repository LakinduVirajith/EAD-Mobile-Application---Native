package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
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
    private val orderApiService = OrderApiService(this)

    // INITIALIZE PAGE NUMBER AND SIZE
    private var pageNumber = 1
    private var pageSize = 10
    private var isLoading = false
    private var isFinish = false

    //  ORDER LIST
    private var orderList = mutableListOf<Order>()

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

        // SETUP ORDERS LIST SCROLL
        setupRecyclerViewScrollListener()
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
        // INITIALIZE THE ADEPTER WITH AND ORDER LIST
        orderAdapter = OrderAdapter(orderList)
        orderRecyclerView.adapter = orderAdapter
        orderRecyclerView.layoutManager = LinearLayoutManager(this)
        loadOrders()
    }

    // FUNCTION TO LOAD ORDERS
    private fun loadOrders() {
        if (isLoading || isFinish) return
        orderApiService.fetchOrders(pageNumber = pageNumber, pageSize = pageSize) { result ->
            runOnUiThread {
                // HANDLE SUCCESS OR FAILURE RESULT
                if (result.isSuccess) {
                    val orders = result.getOrNull() ?: emptyList()
                    orderAdapter.addMoreProducts(orders)

                    if(orders.isEmpty()){
                        isFinish = true
                    }

                    // SHOW OR HIDE THE ORDERS TEXTVIEW BASED ON THE LIST SIZE
                    if (orders.isEmpty() && pageNumber == 1) {
                        orderRecyclerView.visibility = View.GONE
                        emptyOrderText.visibility = View.VISIBLE
                    } else {
                        orderRecyclerView.visibility = View.VISIBLE
                        emptyOrderText.visibility = View.GONE
                    }
                    pageNumber++
                }
                // HANDLE ERROR (SHOW TOAST, LOG ERROR, ETC.)
                else {
                    val error = result.exceptionOrNull()?.message ?: "Load failed. Please check your internet connection or try again."
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
                isLoading = false
            }
        }
    }

    // SETUP RECYCLER VIEW SCROLL LISTENER FOR ENDLESS SCROLLING
    private fun setupRecyclerViewScrollListener() {
        orderRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isFinish && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        loadOrders()
                }
            }
        })
    }
}