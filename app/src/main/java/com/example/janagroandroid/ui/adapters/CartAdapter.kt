package com.example.janagroandroid.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.janagroandroid.R
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.databinding.ItemCartBinding
import java.util.Locale

class CartAdapter(
    private var items: List<CartEntity> = emptyList(),
    private val onDelete: (Long) -> Unit,
    private val onUpdateQty: (Long, Int) -> Unit,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    private val selectedIds = mutableSetOf<Long>()

    fun submitList(newItems: List<CartEntity>) {
        items = newItems
        // By default, select all new items if it was empty or keep current selections
        if (selectedIds.isEmpty()) {
            selectedIds.addAll(newItems.map { it.id })
        } else {
            val newItemIds = newItems.map { it.id }.toSet()
            selectedIds.retainAll { it in newItemIds }
        }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    fun getSelectedItems(): List<CartEntity> {
        return items.filter { it.id in selectedIds }
    }

    fun selectAll(select: Boolean) {
        if (select) {
            selectedIds.addAll(items.map { it.id })
        } else {
            selectedIds.clear()
        }
        notifyDataSetChanged()
        onSelectionChanged()
    }

    fun isAllSelected(): Boolean = items.isNotEmpty() && selectedIds.size == items.size

    inner class VH(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartEntity) {
            binding.tvName.text = item.productName
            binding.tvQty.text = item.qty.toString()
            binding.tvVariant.text = "Varian: Standar" // Placeholder
            binding.tvPrice.text = "Rp ${String.format(Locale.GERMANY, "%,.0f", item.price)}"

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

            binding.cbItem.isChecked = item.id in selectedIds

            binding.cbItem.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedIds.add(item.id)
                } else {
                    selectedIds.remove(item.id)
                }
                onSelectionChanged()
            }

            binding.btnDelete.setOnClickListener {
                onDelete(item.id)
            }

            binding.btnPlus.setOnClickListener {
                onUpdateQty(item.id, item.qty + 1)
            }

            binding.btnMinus.setOnClickListener {
                if (item.qty > 1) {
                    onUpdateQty(item.id, item.qty - 1)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
