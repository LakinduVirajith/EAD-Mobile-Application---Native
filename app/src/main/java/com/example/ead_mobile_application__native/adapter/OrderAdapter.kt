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

class OrderAdapter(private var orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderID: TextView = itemView.findViewById(R.id.cOrderId)
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

    fun updateItems(newOrders: List<Order>) {
        // STORE OLD ORDERS
        val oldOrders = orders
        orders = newOrders

        // NOTIFY ABOUT CHANGES
        if (oldOrders.size < newOrders.size) {
            // NOTIFY ITEM RANGE INSERTED
            notifyItemRangeInserted(oldOrders.size, newOrders.size - oldOrders.size)
        } else if (oldOrders.size > newOrders.size) {
            // NOTIFY ITEM RANGE REMOVED
            notifyItemRangeRemoved(newOrders.size, oldOrders.size - newOrders.size)
        } else {
            // NOTIFY ITEM CHANGES
            for (i in oldOrders.indices) {
                if (oldOrders[i] != newOrders[i]) {
                    notifyItemChanged(i)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // SET TEXT FIELDS
        holder.orderID.text = holder.itemView.context.getString(R.string.order_id_format, order.orderId)
        holder.orderDate.text = order.orderDate
        holder.orderStatus.text = order.status
        holder.totalPrice.text = holder.itemView.context.getString(R.string.price_format, order.totalOrderPrice)

        // LOAD PRODUCT IMAGE USING GLIDE
        Glide.with(holder.itemView.context)
            .load(order.productImageResId)
            .into(holder.productImage)

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
            val intent = Intent(context, OrderDetailsActivity::class.java).apply {
                putExtra("order_id", order.orderId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orders.size
}