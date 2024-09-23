package com.example.ead_mobile_application__native.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.ead_mobile_application__native.R
import androidx.recyclerview.widget.RecyclerView

class BannerAdapter(private val banners: List<Int>) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.banner_item, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(banners[position])
    }

    override fun getItemCount(): Int = banners.size

    class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bannerImageView: ImageView = itemView.findViewById(R.id.bannerImageView)

        fun bind(imageResId: Int) {
            bannerImageView.setImageResource(imageResId)
        }
    }
}