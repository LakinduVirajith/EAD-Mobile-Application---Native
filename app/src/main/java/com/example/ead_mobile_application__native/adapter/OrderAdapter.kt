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
import com.example.ead_mobile_application__native.model.Order
import com.example.ead_mobile_application__native.screen.OrderDetailsActivity
import com.example.ead_mobile_application__native.screen.VendorRankingActivity

class OrderAdapter(private var orders: MutableList<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.cOrderId)
        val orderDate: TextView = itemView.findViewById(R.id.cOrderDate)
        val totalPrice: TextView = itemView.findViewById(R.id.cTotalPrice)
        val orderStatus: TextView = itemView.findViewById(R.id.cOrderStatus)
        val productImage: ImageView = itemView.findViewById(R.id.oProductImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    // FUNCTION TO ADD MORE ORDERS FOR PAGINATION
    fun addMoreProducts(moreOrders: List<Order>) {
        val startPos = orders.size
        orders.addAll(moreOrders)
        notifyItemRangeInserted(startPos, moreOrders.size)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // SET TEXT FIELDS
        holder.orderId.text = order.orderId
        holder.orderDate.text = order.orderDate
        holder.orderStatus.text = order.status
        holder.totalPrice.text = holder.itemView.context.getString(R.string.price_format, order.totalOrderPrice)

        // CHECK IF IMAGE IS A RESOURCE ID OR URL
        val imageUri = order.imageUri
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

        // SET ORDER STATUS COLOR BASED ON STATUS
        val context = holder.itemView.context
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

        // SET BACKGROUND COLOR FOR ORDER STATUS
        holder.orderStatus.setTextColor(ContextCompat.getColor(context, statusColor))

        // SET CLICK LISTENER TO NAVIGATE TO ORDER DETAILS
        holder.itemView.setOnClickListener {
            if(order.status == "Delivered"){
                val intent = Intent(context, VendorRankingActivity::class.java).apply {
                    putExtra("order_id", order.orderId)
                }
                context.startActivity(intent)
            }else{
                val intent = Intent(context, OrderDetailsActivity::class.java).apply {
                    putExtra("order_id", order.orderId)
                }
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int = orders.size
}