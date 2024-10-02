package com.example.ead_mobile_application__native.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ead_mobile_application__native.R
import com.example.ead_mobile_application__native.model.Cart
import com.example.ead_mobile_application__native.screen.ProductDetailsActivity
import com.example.ead_mobile_application__native.service.CartApiService

interface OnCartChangeListener {
    fun onCartChanged(cartItems: List<Cart>)
}

class CartAdapter(
    private var items: MutableList<Cart>,
    private val context: Context,
    private val cartChangeListener: OnCartChangeListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>(){
    // API SERVICE INSTANCE
    private val cartApiService = CartApiService(context)

    // VIEW HOLDER CLASS FOR CART ITEM VIEWS
    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // VIEW REFERENCES FOR CART ITEM COMPONENTS
        val productImage: ImageView = itemView.findViewById(R.id.cProductImage)
        val productName: TextView = itemView.findViewById(R.id.cProductName)
        val productPrice: TextView = itemView.findViewById(R.id.cProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.cProductQuantity)

        val plusIcon: ImageView = itemView.findViewById(R.id.cPlusIcon)
        val minusIcon: ImageView = itemView.findViewById(R.id.cMinusIcon)
        val trashIcon: LinearLayout = itemView.findViewById(R.id.cTrashIcon)
    }

    // INFLATE VIEW HOLDER FOR CART ITEM LAYOUT
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    // GET ITEM COUNT FOR ADAPTER
    override fun getItemCount(): Int {
        return items.size
    }

    // UPDATE CART ITEMS WITH NEW DATA
    fun updateItems(newItems: List<Cart>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // BIND DATA TO VIEW HOLDER
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        loadProductImage(holder, item.imageUri) // LOAD PRODUCT IMAGE
        holder.productName.text = item.productName // SET PRODUCT NAME
        holder.productPrice.text = context.getString(R.string.price_format, calculateDiscountedPrice(item)) // SET PRODUCT PRICE
        holder.productQuantity.text = item.quantity.toString() // SET PRODUCT QUANTITY

        // SET CLICK LISTENER TO NAVIGATE TO PRODUCT DETAILS
        holder.productImage.setOnClickListener { openProductDetails(item.productId) }
        // SET CLICK LISTENER TO INCREASE QUANTITY
        holder.plusIcon.setOnClickListener { updateQuantity(item, position, true) }
        // SET CLICK LISTENER TO DECREES QUANTITY
        holder.minusIcon.setOnClickListener { updateQuantity(item, position, false) }
        // SET CLICK LISTENER TO REMOVE CART ITEM
        holder.trashIcon.setOnClickListener { removeCartItem(item, position) }
    }

    // LOAD PRODUCT IMAGE BASED ON URI OR DEFAULT
    private fun loadProductImage(holder: CartViewHolder, imageUri: String?) {
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
    }

    // CALCULATE DISCOUNTED PRICE OF THE PRODUCT
    private fun calculateDiscountedPrice(item: Cart) = item.price - (item.price * item.discount / 100)

    // OPEN PRODUCT DETAILS ACTIVITY
    private fun openProductDetails(productId: String) {
        val intent = Intent(context, ProductDetailsActivity::class.java).apply {
            putExtra("product_id", productId)
        }
        context.startActivity(intent)
    }

    // UPDATE QUANTITY OF THE CART ITEM
    private fun updateQuantity(item: Cart, position: Int, isIncrease: Boolean) {
        val apiCall = if (isIncrease) cartApiService::cartProductPlus else cartApiService::cartProductMinus

        // CHECK IF DECREASING QUANTITY BELOW 1
        if (!isIncrease && item.quantity <= 1) {
            Toast.makeText(context, "Item quantity can't be less than 1", Toast.LENGTH_SHORT).show()
            return
        }

        // CALL API TO UPDATE QUANTITY
        apiCall(item.cartId) { status, message ->
            (context as? Activity)?.runOnUiThread {
                handleQuantityUpdateResponse(status, message, item, position, isIncrease)
            }
        }
    }

    // HANDLE RESPONSE OF QUANTITY UPDATE API CALL
    private fun handleQuantityUpdateResponse(status: Int?, message: String?, item: Cart, position: Int, isIncrease: Boolean) {
        when (status) {
            200 -> {
                item.quantity += if (isIncrease) 1 else -1
                notifyItemChanged(position)
                cartChangeListener.onCartChanged(items)
                Toast.makeText(context, "$status: $message", Toast.LENGTH_SHORT).show()
            }
            401 -> Toast.makeText(context, "$status: Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(context, "$status: $message", Toast.LENGTH_SHORT).show()
        }
    }

    // REMOVE ITEM FROM CART
    private fun removeCartItem(item: Cart, position: Int) {
        cartApiService.cartProductRemove(item.cartId) { status, message ->
            (context as? Activity)?.runOnUiThread {
                if (status == 200) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    cartChangeListener.onCartChanged(items)
                    Toast.makeText(context, "$status: $message", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "$status: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // SWAP ITEMS IN THE CART
    fun swapItems(fromPosition: Int, toPosition: Int) {
        // SWAP ITEM IN THE LIST
        val temp = items[fromPosition]
        items[fromPosition] = items[toPosition]
        items[toPosition] = temp

        // NOTIFY THE ADAPTER OF ITEM MOVED
        notifyItemMoved(fromPosition, toPosition)
    }
}