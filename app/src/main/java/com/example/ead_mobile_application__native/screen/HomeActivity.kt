package com.example.ead_mobile_application__native.screen

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.ImageView
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeActivity : AppCompatActivity() {
    // DECLARE VIEWS
    private lateinit var viewPager: ViewPager2
    private lateinit var searchEditText: EditText
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    // HANDLER FOR BANNER SLIDING
    private lateinit var handler: Handler
    private var currentPage = 0
    private var searchRunnable: Runnable? = null

    // API SERVICE INSTANCE
    private val productApiService = ProductApiService()

    // SAMPLE PRODUCT LIST
    private var productList = listOf(
        Product(
            id = "00001",
            name = "Product 1",
            price = 10.00,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pretium egestas est, et convallis dolor sollicitudin at. In hac habitasse platea dictumst. Duis eget leo dictum, convallis sapien in, hendrerit augue. Aliquam porttitor justo non libero imperdiet suscipit. Curabitur et nisi nisi. Proin consequat turpis nibh, eu accumsan leo accumsan ac. Duis semper malesuada egestas. Vestibulum cursus nisl ac augue molestie, quis vehicula urna vestibulum. Suspendisse ut rutrum ante. Duis rutrum, nulla at pharetra luctus, erat velit finibus augue, sit amet tincidunt felis mi placerat ipsum. Nullam molestie, velit quis hendrerit mattis, velit justo ultrices libero, nec efficitur nibh sapien vitae nulla. Suspendisse elit neque, placerat vehicula leo sit amet, bibendum condimentum eros. In hac habitasse platea dictumst. Nulla quis mi sapien. Proin hendrerit ante et turpis luctus, non maximus libero scelerisque.",
            imageResId = R.drawable.product_1,
            rating = 4.5f,
            category = "Electronics",
            stockQuantity = 100
        ),
        Product(
            id = "00002",
            name = "Product 2",
            price = 15.00,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pretium egestas est, et convallis dolor sollicitudin at. In hac habitasse platea dictumst. Duis eget leo dictum, convallis sapien in, hendrerit augue. Aliquam porttitor justo non libero imperdiet suscipit. Curabitur et nisi nisi. Proin consequat turpis nibh, eu accumsan leo accumsan ac. Duis semper malesuada egestas. Vestibulum cursus nisl ac augue molestie, quis vehicula urna vestibulum. Suspendisse ut rutrum ante. Duis rutrum, nulla at pharetra luctus, erat velit finibus augue, sit amet tincidunt felis mi placerat ipsum. Nullam molestie, velit quis hendrerit mattis, velit justo ultrices libero, nec efficitur nibh sapien vitae nulla. Suspendisse elit neque, placerat vehicula leo sit amet, bibendum condimentum eros. In hac habitasse platea dictumst. Nulla quis mi sapien. Proin hendrerit ante et turpis luctus, non maximus libero scelerisque.",
            imageResId = R.drawable.product_2,
            rating = 4.0f,
            category = "Clothing",
            stockQuantity = 50
        ),
        Product(
            id = "00003",
            name = "Product 3",
            price = 20.00,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pretium egestas est, et convallis dolor sollicitudin at. In hac habitasse platea dictumst. Duis eget leo dictum, convallis sapien in, hendrerit augue. Aliquam porttitor justo non libero imperdiet suscipit. Curabitur et nisi nisi. Proin consequat turpis nibh, eu accumsan leo accumsan ac. Duis semper malesuada egestas. Vestibulum cursus nisl ac augue molestie, quis vehicula urna vestibulum. Suspendisse ut rutrum ante. Duis rutrum, nulla at pharetra luctus, erat velit finibus augue, sit amet tincidunt felis mi placerat ipsum. Nullam molestie, velit quis hendrerit mattis, velit justo ultrices libero, nec efficitur nibh sapien vitae nulla. Suspendisse elit neque, placerat vehicula leo sit amet, bibendum condimentum eros. In hac habitasse platea dictumst. Nulla quis mi sapien. Proin hendrerit ante et turpis luctus, non maximus libero scelerisque.",
            imageResId = R.drawable.product_3,
            rating = 4.2f,
            category = "Accessories",
            stockQuantity = 75
        ),
        Product(
            id = "00004",
            name = "Product 4",
            price = 25.00,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pretium egestas est, et convallis dolor sollicitudin at. In hac habitasse platea dictumst. Duis eget leo dictum, convallis sapien in, hendrerit augue. Aliquam porttitor justo non libero imperdiet suscipit. Curabitur et nisi nisi. Proin consequat turpis nibh, eu accumsan leo accumsan ac. Duis semper malesuada egestas. Vestibulum cursus nisl ac augue molestie, quis vehicula urna vestibulum. Suspendisse ut rutrum ante. Duis rutrum, nulla at pharetra luctus, erat velit finibus augue, sit amet tincidunt felis mi placerat ipsum. Nullam molestie, velit quis hendrerit mattis, velit justo ultrices libero, nec efficitur nibh sapien vitae nulla. Suspendisse elit neque, placerat vehicula leo sit amet, bibendum condimentum eros. In hac habitasse platea dictumst. Nulla quis mi sapien. Proin hendrerit ante et turpis luctus, non maximus libero scelerisque.",
            imageResId = R.drawable.product_4,
            rating = 4.8f,
            category = "Electronics",
            stockQuantity = 200
        ),
        Product(
            id = "00005",
            name = "Product 5",
            price = 30.00,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pretium egestas est, et convallis dolor sollicitudin at. In hac habitasse platea dictumst. Duis eget leo dictum, convallis sapien in, hendrerit augue. Aliquam porttitor justo non libero imperdiet suscipit. Curabitur et nisi nisi. Proin consequat turpis nibh, eu accumsan leo accumsan ac. Duis semper malesuada egestas. Vestibulum cursus nisl ac augue molestie, quis vehicula urna vestibulum. Suspendisse ut rutrum ante. Duis rutrum, nulla at pharetra luctus, erat velit finibus augue, sit amet tincidunt felis mi placerat ipsum. Nullam molestie, velit quis hendrerit mattis, velit justo ultrices libero, nec efficitur nibh sapien vitae nulla. Suspendisse elit neque, placerat vehicula leo sit amet, bibendum condimentum eros. In hac habitasse platea dictumst. Nulla quis mi sapien. Proin hendrerit ante et turpis luctus, non maximus libero scelerisque.",
            imageResId = R.drawable.product_5,
            rating = 4.1f,
            category = "Home",
            stockQuantity = 30
        ),
        Product(
            id = "00006",
            name = "Product 6",
            price = 35.00,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam pretium egestas est, et convallis dolor sollicitudin at. In hac habitasse platea dictumst. Duis eget leo dictum, convallis sapien in, hendrerit augue. Aliquam porttitor justo non libero imperdiet suscipit. Curabitur et nisi nisi. Proin consequat turpis nibh, eu accumsan leo accumsan ac. Duis semper malesuada egestas. Vestibulum cursus nisl ac augue molestie, quis vehicula urna vestibulum. Suspendisse ut rutrum ante. Duis rutrum, nulla at pharetra luctus, erat velit finibus augue, sit amet tincidunt felis mi placerat ipsum. Nullam molestie, velit quis hendrerit mattis, velit justo ultrices libero, nec efficitur nibh sapien vitae nulla. Suspendisse elit neque, placerat vehicula leo sit amet, bibendum condimentum eros. In hac habitasse platea dictumst. Nulla quis mi sapien. Proin hendrerit ante et turpis luctus, non maximus libero scelerisque.",
            imageResId = R.drawable.product_6,
            rating = 4.7f,
            category = "Electronics",
            stockQuantity = 60
        )
    )

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

        // SET CLICK LISTENER FOR SEARCH ICON
        val searchIcon = findViewById<ImageView>(R.id.hSearchIcon)
        searchIcon.setOnClickListener {
            performSearch(searchEditText.text.toString())
        }

        // SETUP NAVIGATION BAR
        setupBottomNavigationView()

        // SETUP BANNER VIEW PAGER
        setupBannerView()

        // SETUP SEARCH FUNCTIONALITY
        setupSearchFunctionality()

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
        productAdapter = ProductAdapter(productList)
        productRecyclerView.adapter = productAdapter
        productRecyclerView.layoutManager = GridLayoutManager(this, 2)

        return

        productApiService.homeProducts() { response ->
            runOnUiThread {
                // UPDATE PRODUCT LIST BASED ON RESPONSE
                if (response != null) {
                    val gson = Gson()
                    val productType = object : TypeToken<List<Product>>() {}.type
                    updateProductList(gson.fromJson(response, productType))
                }
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

    // FUNCTION TO UPDATE PRODUCT LIST
    private fun updateProductList(products: List<Product>) {
        productList = products
        productAdapter.updateProducts(products)
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