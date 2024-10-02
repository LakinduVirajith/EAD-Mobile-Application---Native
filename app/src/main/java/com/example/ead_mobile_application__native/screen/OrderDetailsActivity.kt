package com.example.ead_mobile_application__native.screen

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.adapter.OrderDetailsAdapter
import com.example.ead_mobile_application__native.model.Order
import com.example.ead_mobile_application__native.model.OrderDetails
import com.example.ead_mobile_application__native.service.OrderApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class OrderDetailsActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var orderDetailsRecyclerView: RecyclerView
    private lateinit var orderDetailsAdapter: OrderDetailsAdapter

    // API SERVICE INSTANCE
    private val orderApiService = OrderApiService(this)

    // SAMPLE ORDER DETAIL LIST
    private var detailList = listOf(
        OrderDetails(
            productId = 1,
            productName = "Casual Cotton T-Shirt",
            imageResId = R.drawable.product_1,
            price = 19.99,
            quantity = 1,
            size = "M",
            color = "Black"
        ),
        OrderDetails(
            productId = 2,
            productName = "Classic Chino Pants",
            imageResId = R.drawable.product_4,
            price = 34.99,
            quantity = 2,
            size = "L",
            color = "Yellow"
        )
    )

    // SAMPLE ORDER DETAILS
    private var order: Order = Order(
        orderId = 1,
        productImageResId = R.drawable.product_2,
        orderDate = "2024/09/12",
        status = "Pending",
        totalOrderPrice = 245.65
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order_details)

        // SET THE STATUS BAR BACKGROUND COLOR
        window.statusBarColor = getColor(R.color.black20)

        // SET UP THE TOOLBAR
        val toolbar: Toolbar = findViewById(R.id.odToolbar)
        setSupportActionBar(toolbar)

        // ENABLE THE DEFAULT BACK BUTTON IN THE ACTION BAR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.orderDetailsActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FIND UI COMPONENTS BY ID
        orderDetailsRecyclerView = findViewById(R.id.odRecyclerView)

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
        // GET THE ORDER ID FROM INTENT AND SET THE ORDER ID
        supportActionBar?.title = ""
        val orderIDTextView: TextView = findViewById(R.id.odOrderID)
        val orderId = intent.getIntExtra("order_id", 0)
        orderIDTextView.text = String.format(getString(R.string.order_id_format), orderId)

//        // FETCH ORDER DETAILS
//        orderApiService.fetchOrder(orderId) { response ->
//            runOnUiThread {
//                // UPDATE ORDER BASED ON RESPONSE
//                if (response != null) {
//                    val gson = Gson()
//                    val productType = object : TypeToken<List<Order>>() {}.type
//                    order = gson.fromJson(response, productType)
//                }
//            }
//        }
//
//        // FETCH ORDER ITEMS DETAILS
//        orderApiService.fetchOrderDetails(orderId) { response ->
//            runOnUiThread {
//                // UPDATE DETAILS LIST BASED ON RESPONSE
//                if (response != null) {
//                    val gson = Gson()
//                    val productType = object : TypeToken<List<Order>>() {}.type
//                    updateList(gson.fromJson(response, productType))
//                }
//            }
//        }

        // SET ORDER DETAILS
        val orderDateTextView: TextView = findViewById(R.id.odOrderDate)
        val orderStatusTextView: TextView = findViewById(R.id.odOrderStatus)
        val orderTotalPriceTextView: TextView = findViewById(R.id.odOrderTotalPrice)
        orderDateTextView.text = order.orderDate
        orderStatusTextView.text = order.status
        orderTotalPriceTextView.text = getString(R.string.price_format, order.totalOrderPrice)

        // SET STATUS COLOR
        val statusColor = when (order.status) {
            "Pending" -> R.color.primeOrange
            "Processing" -> R.color.primeBlue
            "Shipped" -> R.color.primeBlue
            "Delivered" -> R.color.primeGreen
            "Completed" -> R.color.primeGreen
            "Cancelled" -> R.color.primeRed
            "Refunded" -> R.color.primeYellow
            "Returned" -> R.color.primePurple
            else -> R.color.primeGray
        }
        orderStatusTextView.setTextColor(ContextCompat.getColor(this, statusColor))

        // INITIALIZE THE ADEPTER WITH AND ORDER DETAILS LIST
        orderDetailsAdapter = OrderDetailsAdapter(detailList)
        orderDetailsRecyclerView.adapter = orderDetailsAdapter
        orderDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    // FUNCTION TO UPDATE ORDER LIST
    private fun updateList(details: List<OrderDetails>) {
        detailList = details
        orderDetailsAdapter.updateItems(details)
    }
}