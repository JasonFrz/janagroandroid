package com.example.janagroandroid.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.janagroandroid.data.local.entity.HistoryEntity
import com.example.janagroandroid.databinding.ItemHistoryBinding

class HistoryAdapter(
    private var items: List<HistoryEntity> = emptyList()
) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    fun submitList(newItems: List<HistoryEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryEntity) {
            binding.tvDate.text = item.date
            binding.tvTotal.text = "Total: Rp ${item.total}"
            binding.tvStatus.text = item.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}