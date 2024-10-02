package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.adapter.BannerAdapter
import com.example.ead_mobile_application__native.adapter.ProductAdapter
import com.example.ead_mobile_application__native.model.Product
import com.example.ead_mobile_application__native.service.ProductApiService
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var viewPager: ViewPager2
    private lateinit var searchEditText: EditText
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var noProductTextView: TextView

    // HANDLER FOR BANNER SLIDING
    private lateinit var handler: Handler
    private var currentPage = 0
    private var searchRunnable: Runnable? = null

    // API SERVICE INSTANCE
    private val productApiService = ProductApiService(this)

    // INITIALIZE PAGE NUMBER AND SIZE
    private var pageNumber = 1
    private var pageSize = 4
    private var currentSearchQuery: String? = null
    private var isLoading = false

    // PRODUCT LIST
    private var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeActivity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FIND UI COMPONENTS BY ID
        viewPager = findViewById(R.id.hBannerViewPager)
        searchEditText = findViewById(R.id.hSearch)
        productRecyclerView = findViewById(R.id.hProductRecyclerView)
        noProductTextView = findViewById(R.id.hNoProductTextView)

        // SET CLICK LISTENER FOR SEARCH ICON
        val searchIcon = findViewById<ImageView>(R.id.hSearchIcon)
        searchIcon.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()

        // SETUP BANNER VIEW PAGER
        setupBannerView()

        // SETUP PRODUCT LIST AND ADAPTER
        setupProductList()

        // SETUP SEARCH FUNCTIONALITY
        setupSearchFunctionality()

        // SETUP PRODUCT PRODUCT LIST SCROLL
        setupRecyclerViewScrollListener()
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

    // FUNCTION TO SETUP PRODUCT LIST AND ADAPTER WITH ENDLESS SCROLLING
    private fun setupProductList() {
        productAdapter = ProductAdapter(productList)
        productRecyclerView.adapter = productAdapter
        productRecyclerView.layoutManager = GridLayoutManager(this, 2)
        loadProducts()
    }

    // FUNCTION TO LOAD PRODUCTS
    private fun loadProducts() {
        if (isLoading) return
        isLoading = true
        productApiService.fetchHomeProducts(pageNumber = pageNumber, pageSize = pageSize) { result ->
            runOnUiThread {
                // HANDLE SUCCESS OR FAILURE RESULT
                if (result.isSuccess) {
                    val products = result.getOrNull() ?: emptyList()
                    productAdapter.addMoreProducts(products)

                    // SHOW OR HIDE THE PRODUCTS TEXTVIEW BASED ON THE LIST SIZE
                    if (products.isEmpty() && pageNumber == 1) {
                        noProductTextView.visibility = View.VISIBLE
                        noProductTextView.text = getString(R.string.no_product)
                    } else {
                        noProductTextView.visibility = View.GONE
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

    // FUNCTION TO PERFORM SEARCH
    private fun performSearch(query: String, isPagination: Boolean = false) {
        // CHECK IF SEARCH FIELDS IS FILLED
        if (query.isNotEmpty()) {
            if (!isPagination) {
                productAdapter.updateProducts(emptyList())
                pageNumber = 1 // REST PAGE ONLY WHEN IT'S A NEW SEARCH
            }

            currentSearchQuery = query
            Toast.makeText(this, "Looking for '$query'. Please wait...", Toast.LENGTH_LONG).show()
            // CALL SEARCH METHOD FROM API SERVICE
            productApiService.searchProducts(search =  query, pageSize = pageSize, pageNumber = pageNumber) { result ->
                runOnUiThread {
                    // UPDATE PRODUCT LIST BASED ON RESPONSE
                    if (result.isSuccess) {
                        val products = result.getOrNull() ?: emptyList()
                        if(pageSize == 1){
                            productAdapter.updateProducts(products)
                        }else{
                            productAdapter.addMoreProducts(products)
                        }

                        // CHECK IF NO PRODUCTS WERE FOUND
                        if (products.isEmpty() && pageNumber == 1) {
                            noProductTextView.visibility = View.VISIBLE
                            noProductTextView.text = getString(R.string.search_error, query)
                        } else {
                            noProductTextView.visibility = View.GONE
                        }
                    }
                    // HANDLE ERROR (SHOW TOAST, LOG ERROR, ETC.)
                    else {
                        val error = result.exceptionOrNull()?.message ?: "Search failed. Please check your internet connection or try again."
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            currentSearchQuery = null
            pageNumber = 1
            productAdapter.updateProducts(emptyList())
            loadProducts()
        }
    }

    // SETUP RECYCLER VIEW SCROLL LISTENER FOR ENDLESS SCROLLING
    private fun setupRecyclerViewScrollListener() {
        productRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    if (currentSearchQuery != null && currentSearchQuery!!.isNotEmpty()) {
                        pageNumber++
                        performSearch(currentSearchQuery!!, isPagination = true)
                    } else {
                        loadProducts()
                    }
                }
            }
        })
    }

    // STOP SLIDING TO PREVENT MEMORY LEAKS
    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}