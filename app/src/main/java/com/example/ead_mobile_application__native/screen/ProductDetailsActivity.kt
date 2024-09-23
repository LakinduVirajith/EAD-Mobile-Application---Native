package com.example.ead_mobile_application__native.screen

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ead_mobile_application__native.R

class ProductDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_details)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // GET THE PRODUCT NAME FROM INTENT
        val productName = intent.getStringExtra("product_name")

        // FIND THE TEXT VIEW AND SET THE PRODUCT NAME
        val productNameTextView: TextView = findViewById(R.id.pdProductName)
        productNameTextView.text = productName ?: "Product name not available"
    }
}