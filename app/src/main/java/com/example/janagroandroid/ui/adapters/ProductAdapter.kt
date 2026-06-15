package com.example.janagroandroid.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.janagroandroid.R
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.databinding.ItemProductBinding

class ProductAdapter(
    private var items: List<ProductEntity> = emptyList(),
    private val onClick: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    fun submitList(newItems: List<ProductEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductEntity) {
            binding.tvName.text = item.name
            binding.tvPrice.text = "Rp ${item.price}"
            binding.tvStock.text = "Stok: ${item.stock}"

            val imageSource = if (item.imageUrl.isNullOrEmpty()) {
                R.drawable.farmer
            } else {
                item.imageUrl
            }

            Glide.with(binding.root.context)
                .load(imageSource)
                .placeholder(R.drawable.farmer)
                .error(R.drawable.farmer)
                .into(binding.ivProduct)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}