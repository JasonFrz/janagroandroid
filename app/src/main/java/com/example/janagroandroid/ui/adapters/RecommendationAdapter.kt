package com.example.janagroandroid.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.databinding.ItemRecommendationBinding
import java.util.Locale

class RecommendationAdapter(
    private var items: List<ProductEntity> = emptyList(),
    private val onClick: (ProductEntity) -> Unit
) : RecyclerView.Adapter<RecommendationAdapter.VH>() {

    fun submitList(newItems: List<ProductEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductEntity) {
            binding.tvName.text = item.name
            binding.tvPrice.text = "Rp ${String.format(Locale.GERMANY, "%,.0f", item.price)}"

            Glide.with(binding.root.context)
                .load(item.imageUrl)
                .placeholder(com.example.janagroandroid.R.drawable.sawid)
                .into(binding.ivProduct)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
