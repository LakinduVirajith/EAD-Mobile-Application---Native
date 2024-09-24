package com.example.ead_mobile_application__native.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.Product
import com.example.ead_mobile_application__native.screen.ProductDetailsActivity

class ProductAdapter(private var products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.hProductImage)
        val productName: TextView = itemView.findViewById(R.id.hProductName)
        val productPrice: TextView = itemView.findViewById(R.id.hProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    fun updateProducts(newProducts: List<Product>) {
        // STORE OLD PRODUCTS
        val oldProducts = products
        products = newProducts

        // NOTIFY ABOUT CHANGES
        if (oldProducts.size < newProducts.size) {
            // NOTIFY ITEM RANGE INSERTED
            notifyItemRangeInserted(oldProducts.size, newProducts.size - oldProducts.size)
        } else if (oldProducts.size > newProducts.size) {
            // NOTIFY ITEM RANGE REMOVED
            notifyItemRangeRemoved(newProducts.size, oldProducts.size - newProducts.size)
        } else {
            // NOTIFY ITEM CHANGES
            for (i in oldProducts.indices) {
                if (oldProducts[i] != newProducts[i]) {
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        Glide.with(holder.itemView.context)
            .load(product.imageResId)
            .into(holder.productImage)
        holder.productName.text = product.name
        holder.productPrice.text = holder.itemView.context.getString(R.string.product_price_format, product.price.toString())

        // SET CLICK LISTENER TO NAVIGATE TO PRODUCT DETAILS
        holder.itemView.setOnClickListener {
            // CREATE AN INTENT TO START THE PRODUCT DETAILS ACTIVITY OR FRAGMENT
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                putExtra("product_id", product.id)
                putExtra("product_name", product.name)
                putExtra("product_price", product.price)
                putExtra("product_description", product.description)
                putExtra("product_image", product.imageResId)
                putExtra("product_rating", product.rating)
                putExtra("product_category", product.category)
                putExtra("product_stock_quantity", product.stockQuantity)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = products.size
}