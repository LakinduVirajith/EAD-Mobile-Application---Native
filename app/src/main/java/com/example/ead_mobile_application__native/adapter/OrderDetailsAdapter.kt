package com.example.ead_mobile_application__native.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.OrderProductDetails
import com.example.ead_mobile_application__native.screen.ProductDetailsActivity

class OrderDetailsAdapter (private var productDetails: List<OrderProductDetails>) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {
    inner class OrderDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.odProductName)
        val productPrice: TextView = itemView.findViewById(R.id.odProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.odProductQuantity)
        val productSize: TextView = itemView.findViewById(R.id.odProductSize)
        val productColor: TextView = itemView.findViewById(R.id.odProductColor)
        val productTotalPrice: TextView = itemView.findViewById(R.id.odProductTotalPrice)
        val productStatus: TextView = itemView.findViewById(R.id.odProductStatus)
        val productImage: ImageView = itemView.findViewById(R.id.odProductImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_details_item, parent, false)
        return OrderDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        val details = productDetails[position]

        // SET TEXT FIELDS
        holder.productName.text = details.productName
        holder.productPrice.text = holder.itemView.context.getString(R.string.price_format, details.price)
        holder.productQuantity.text = details.quantity.toString()
        holder.productSize.text = details.size
        holder.productColor.text = details.color
        val totalOrderPrice = details.quantity.toFloat() * details.price
        holder.productTotalPrice.text = holder.itemView.context.getString(R.string.price_format, totalOrderPrice)
        holder.productStatus.text = details.status

        // SET ORDER STATUS COLOR BASED ON STATUS
        val context = holder.itemView.context
        val statusColor = when (details.status) {
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

        // SET BACKGROUND COLOR FOR ORDER STATUS
        holder.productStatus.setTextColor(ContextCompat.getColor(context, statusColor))

        // CHECK IF IMAGE IS A RESOURCE ID OR URL
        val imageUri = details.imageUri
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

        // SET CLICK LISTENER TO NAVIGATE TO PRODUCT DETAILS
        holder.productImage.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                putExtra("product_id", details.productId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = productDetails.size
}