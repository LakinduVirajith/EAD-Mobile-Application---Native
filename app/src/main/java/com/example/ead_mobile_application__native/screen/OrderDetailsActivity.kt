package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
import com.example.ead_mobile_application__native.model.OrderDetails
import com.example.ead_mobile_application__native.service.OrderApiService

class OrderDetailsActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var orderDetailsRecyclerView: RecyclerView
    private lateinit var orderDetailsAdapter: OrderDetailsAdapter

    // API SERVICE INSTANCE
    private val orderApiService = OrderApiService(this)

    // ORDER DETAIL AND LIST ORDER PRODUCT DETAILS
    private lateinit var orderDetails: OrderDetails;

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
        val orderIDTextView: TextView = findViewById(R.id.odOrderIdTitle)
        val orderId = intent.getStringExtra("order_id")
        orderIDTextView.text = orderId

        // FETCH ORDER DETAILS INFORMATION'S
        orderApiService.fetchOrderDetails(orderId) { result ->
            runOnUiThread {
                if (result.isSuccess) {
                    orderDetails = result.getOrNull()!!

                    // INITIALIZE THE ADAPTER ONCE WITH ORDER DETAILS
                    initializeAdapter()

                    // SETUP PRODUCT VIEW DETAILS
                    setupProductViewDetails()
                }
                else {
                    val error = result.exceptionOrNull()?.message ?: "Order Details load failed. Please check your internet connection or try again."
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // INITIALIZE ADAPTER ONCE WITH ORDER DETAILS
    private fun initializeAdapter() {
        // INITIALIZE THE ADAPTER AND ASSIGN IT
        orderDetailsAdapter = OrderDetailsAdapter(orderDetails.orderItemDetails)
        orderDetailsRecyclerView.adapter = orderDetailsAdapter
        orderDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    // FUNCTION TO SETUP VIEW DETAILS
    private fun setupProductViewDetails() {
        // INITIALIZE VIEWS
        val orderIdTextView: TextView = findViewById(R.id.odOrderId)
        val orderDateTextView: TextView = findViewById(R.id.odOrderDate)
        val orderStatusTextView: TextView = findViewById(R.id.odOrderStatus)
        val orderTotalPriceTextView: TextView = findViewById(R.id.odOrderTotalPrice)

        val userNameTextView: TextView = findViewById(R.id.odUserName)
        val phoneNumberTextView: TextView = findViewById(R.id.odPhoneNumber)
        val shippingAddressTextView: TextView = findViewById(R.id.odShippingAddress)
        val cityTextView: TextView = findViewById(R.id.odCity)
        val stateTextView: TextView = findViewById(R.id.odState)
        val postalCodeTextView: TextView = findViewById(R.id.odPostalCode)

        orderIdTextView.text = orderDetails.orderId
        orderDateTextView.text = orderDetails.orderDate
        orderStatusTextView.text = orderDetails.status
        orderTotalPriceTextView.text = getString(R.string.price_format, orderDetails.totalOrderPrice)

        userNameTextView.text = orderDetails.userName
        phoneNumberTextView.text = orderDetails.phoneNumber
        shippingAddressTextView.text = orderDetails.address
        cityTextView.text = orderDetails.city
        stateTextView.text = orderDetails.state
        postalCodeTextView.text = orderDetails.postalCode

        // SET STATUS COLOR
        val statusColor = when (orderDetails.status) {
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

        val cancelOrderButton: Button = findViewById(R.id.odBtnCancelOrder)
        if(orderDetails.status == "Pending" || orderDetails.status == "Processing"){
            cancelOrderButton.visibility = View.VISIBLE
        }else{
            cancelOrderButton.visibility = View.GONE
        }

        cancelOrderButton.setOnClickListener{
            val intent = Intent(this, CancelOrderActivity::class.java).apply {
                putExtra("product_id", orderDetails.orderId)
            }
            startActivity(intent)
            finish()
        }
    }
}