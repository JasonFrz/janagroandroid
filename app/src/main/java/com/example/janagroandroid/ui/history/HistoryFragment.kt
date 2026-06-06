package com.example.janagroandroid.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class HistoryFragment : Fragment() {

    private val viewModel: HistoryViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val orders by viewModel.orders.observeAsState(emptyList())
                val categories by viewModel.categories.observeAsState(emptyList())
                val isLoading by viewModel.isLoading.observeAsState(false)
                val buyAgainStatus by viewModel.buyAgainStatus.observeAsState()

                if (buyAgainStatus == true) {
                    Toast.makeText(context, "Produk berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.cartFragment)
                    viewModel.resetBuyAgainStatus()
                } else if (buyAgainStatus == false) {
                    Toast.makeText(context, "Gagal menambahkan produk ke keranjang", Toast.LENGTH_SHORT).show()
                    viewModel.resetBuyAgainStatus()
                }

                HistoryScreen(
                    orders = orders,
                    categories = categories,
                    isLoading = isLoading,
                    onBackClick = { findNavController().navigateUp() },
                    onBuyAgainClick = { id -> viewModel.buyAgain(id) },
                    onDetailClick = { id ->
                        // Optional: Navigate to detail
                        Toast.makeText(context, "Detail Pesanan #$id", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchOrders()
    }
}
