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
import com.example.ead_mobile_application__native.model.OrderDetails
import com.example.ead_mobile_application__native.screen.ProductDetailsActivity

class OrderDetailsAdapter (private var orderDetails: List<OrderDetails>) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {
    inner class OrderDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.odProductName)
        val productPrice: TextView = itemView.findViewById(R.id.odProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.odProductQuantity)
        val productSize: TextView = itemView.findViewById(R.id.odProductSize)
        val productColor: TextView = itemView.findViewById(R.id.odProductColor)
        val productTotalPrice: TextView = itemView.findViewById(R.id.odProductTotalPrice)
        val productImage: ImageView = itemView.findViewById(R.id.odProductImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_details_item, parent, false)
        return OrderDetailsViewHolder(view)
    }

    fun updateItems(newOrderDetails: List<OrderDetails>) {
        // STORE OLD ORDER DETAILS
        val oldOrderDetails = orderDetails
        orderDetails = newOrderDetails

        // NOTIFY ABOUT CHANGES
        if (oldOrderDetails.size < newOrderDetails.size) {
            // NOTIFY ITEM RANGE INSERTED
            notifyItemRangeInserted(oldOrderDetails.size, newOrderDetails.size - oldOrderDetails.size)
        } else if (oldOrderDetails.size > newOrderDetails.size) {
            // NOTIFY ITEM RANGE REMOVED
            notifyItemRangeRemoved(newOrderDetails.size, oldOrderDetails.size - newOrderDetails.size)
        } else {
            // NOTIFY ITEM CHANGES
            for (i in oldOrderDetails.indices) {
                if (oldOrderDetails[i] != newOrderDetails[i]) {
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        val orderDetails = orderDetails[position]

        // SET TEXT FIELDS
        holder.productName.text = orderDetails.productName
        holder.productPrice.text = holder.itemView.context.getString(R.string.price_format, orderDetails.price)
        holder.productQuantity.text = orderDetails.quantity.toString()
        holder.productSize.text = orderDetails.size
        holder.productColor.text = orderDetails.color
        val totalOrderPrice = orderDetails.quantity.toFloat() * orderDetails.price
        holder.productTotalPrice.text = holder.itemView.context.getString(R.string.price_format, totalOrderPrice)

        // LOAD PRODUCT IMAGE USING GLIDE
        Glide.with(holder.itemView.context)
            .load(orderDetails.imageResId)
            .into(holder.productImage)

        // SET CLICK LISTENER TO NAVIGATE TO PRODUCT DETAILS
        holder.productImage.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                putExtra("product_id", orderDetails.productId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orderDetails.size
}