package com.example.janagroandroid.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.janagroandroid.data.local.entity.TransactionEntity
import com.example.janagroandroid.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private var items: List<TransactionEntity> = emptyList()
) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun submitList(newItems: List<TransactionEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionEntity) {
            binding.tvDate.text = dateFormat.format(Date(item.createdAt))
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