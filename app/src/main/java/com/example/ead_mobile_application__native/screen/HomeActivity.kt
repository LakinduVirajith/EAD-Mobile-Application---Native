package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.adapter.BannerAdapter
import com.example.ead_mobile_application__native.model.Customer
import com.example.ead_mobile_application__native.model.Product
import com.example.ead_mobile_application__native.service.ProductApiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var viewPager: ViewPager2
    private lateinit var handler: Handler
    private var currentPage = 0
    private lateinit var searchEditText: EditText
    private var searchRunnable: Runnable? = null

    // INSTANCE OF THE PRODUCT API SERVICE
    private val productApiService = ProductApiService()
//    private lateinit var productAdapter: ProductAdapter
//    private var productList: List<Product> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // HANDLE WINDOW INSETS FOR EDGE-TO-EDGE DISPLAY
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // INITIALIZE VIEWS
        viewPager = findViewById(R.id.bannerViewPager)
        searchEditText = findViewById(R.id.pSearch)
        val searchIcon = findViewById<ImageView>(R.id.pSearchIcon)

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()
        // SETUP BANNER VIEW PAGER
        setupBannerView()

        // SETUP SEARCH FUNCTIONALITY
        setupSearchFunctionality()
        // SETUP CLICK LISTENER FOR SEARCH ICON
        searchIcon.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        // SETUP PRODUCT LIST AND ADAPTER
        setupProductList()
    }

    // SETUP BOTTOM NAVIGATION VIEW AND ITS ITEM SELECTION
    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.nvHome

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nvHome -> true
                R.id.nvOrder -> {
                    startActivity(Intent(applicationContext, OrderActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }
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

    // FUNCTION TO SETUP BANNER VIEW PAGER
    private fun setupBannerView(){
        // BANNER IMAGES - LOCALLY STORED
        val bannerImages = listOf(
            R.drawable.banner_1,  // BANNER IMAGE 1
            R.drawable.banner_2,  // BANNER IMAGE 2
            R.drawable.banner_3   // BANNER IMAGE 3
        )

        // SET ADAPTER FOR VIEWPAGER
        val adapter = BannerAdapter(bannerImages)
        viewPager.adapter = adapter

        // START AUTO SLIDING BANNERS
        startAutoSlide()
    }

    // FUNCTION TO AUTO SLIDE EVERY 5 SECONDS
    private fun startAutoSlide() {
        handler = Handler(Looper.getMainLooper())
        val slideRunnable = object : Runnable {
            override fun run() {
                currentPage = (currentPage + 1) % viewPager.adapter!!.itemCount
                viewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, 5000) // Auto slide every 5 seconds
            }
        }
        handler.postDelayed(slideRunnable, 5000)

        // PAUSE SLIDE WHEN THE USER IS INTERACTING WITH THE VIEWPAGER
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }
        })
    }

    // FUNCTION TO SETUP PRODUCT LIST AND ADAPTER
    private fun setupProductList() {
        // INITIALIZE THE ADEPTER WITH AND EMPTY PRODUCT LIST
//        productAdapter = ProductAdapter(productList)
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.adapter = productAdapter
    }

    // FUNCTION TO SETUP SEARCH FUNCTIONALITY
    private fun setupSearchFunctionality() {
        handler = Handler(Looper.getMainLooper())
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // REMOVE ANY EXISTING CALLBACKS IF SEARCH-RUNNABLE IS NOT NULL
                searchRunnable?.let {
                    handler.removeCallbacks(it)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // SET UP THE RUNNABLE TO PERFORM THE SEARCH AFTER 2-SECONDS DELAY
                searchRunnable = Runnable {
                    performSearch(s.toString())
                }
                handler.postDelayed(searchRunnable!!, 4000) // 4000 ms = 4 seconds
            }
        })
    }

    // FUNCTION TO UPDATE PRODUCT LIST
    private fun updateProductList(products: List<Product>) {
//        productList = products
//        productAdapter.updateProducts(products)
//        productAdapter.notifyDataSetChanged()
    }

    // FUNCTION TO PERFORM SEARCH
    private fun performSearch(query: String) {
        Toast.makeText(this, "Searching for: $query", Toast.LENGTH_SHORT).show()
        return

        // CHECK IF SEARCH FIELDS IS FILLED
        if (query != "") {
            // CALL SEARCH METHOD FROM API SERVICE
            productApiService.searchProducts(query) { response ->
                runOnUiThread {
                    // UPDATE PRODUCT LIST BASED ON RESPONSE
                    if (response != null) {
                        val gson = Gson()
                        val productType = object : TypeToken<List<Product>>() {}.type
                        updateProductList(gson.fromJson(response, productType))
                    }
                }
            }
        } else {
            // ALERT USER TO FILL SEARCH FIELD
            Toast.makeText(this, "Search Field is Empty", Toast.LENGTH_SHORT).show()
        }
    }

    // STOP SLIDING TO PREVENT MEMORY LEAKS
    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}