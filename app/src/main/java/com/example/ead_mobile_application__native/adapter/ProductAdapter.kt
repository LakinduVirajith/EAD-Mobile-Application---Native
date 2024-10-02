package com.example.ead_mobile_application__native.adapter

import android.content.Intent
import android.graphics.Paint
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

class ProductAdapter(private var products: MutableList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.hProductImage)
        val productName: TextView = itemView.findViewById(R.id.hProductName)
        val productPrice: TextView = itemView.findViewById(R.id.hProductPrice)
        val productDiscount: TextView = itemView.findViewById(R.id.hDiscountPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    // FUNCTION TO UPDATE PRODUCTS AND NOTIFY THE ADAPTER
    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    // FUNCTION TO ADD MORE PRODUCTS FOR PAGINATION
    fun addMoreProducts(moreProducts: List<Product>) {
        val startPos = products.size
        products.addAll(moreProducts)
        notifyItemRangeInserted(startPos, moreProducts.size)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        // CHECK IF IMAGE IS A RESOURCE ID OR URL
        val imageUri = product.imageUri
        if (imageUri != null && imageUri.startsWith("R.drawable")) {
            // EXTRACT DRAWABLE NAME AND GET ITS ID
            val drawableName = imageUri.substringAfter("R.drawable.")
            val drawableId = holder.itemView.context.resources.getIdentifier(drawableName, "drawable", holder.itemView.context.packageName)

            // LOAD THE IMAGE OR FALLBACK TO DEFAULT
            Glide.with(holder.itemView.context)
                .load(if (drawableId != 0) drawableId else R.drawable.no_image)
                .into(holder.productImage)
        } else {
            // LOAD URL OR DEFAULT IMAGE
            Glide.with(holder.itemView.context)
                .load(imageUri ?: R.drawable.no_image)
                .into(holder.productImage)
        }

        holder.productName.text = if (product.name.length > 15) "${product.name.substring(0, 15)}..." else product.name
        holder.productPrice.text = holder.itemView.context.getString(R.string.price_format, product.price)
        holder.productPrice.paintFlags =  holder.productPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        // CALCULATE AND SET THE DISCOUNTABLE PRICE
        val discountPrice = product.price - (product.price * product.discount / 100)
        holder.productDiscount.text = holder.itemView.context.getString(R.string.price_format, discountPrice)

        // SET CLICK LISTENER TO NAVIGATE TO PRODUCT DETAILS
        holder.itemView.setOnClickListener {
            // CREATE AN INTENT TO START THE PRODUCT DETAILS ACTIVITY OR FRAGMENT
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                putExtra("product_id", product.productId)
            }
            context.startActivity(intent)
        }
    }
}