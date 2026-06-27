package com.example.janagroandroid.ui.seller

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
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class SellerOrdersFragment : Fragment() {

    private val viewModel: SellerOrdersViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val orders by viewModel.orders.observeAsState(initial = emptyList())
                val isLoading by viewModel.isLoading.observeAsState(initial = false)

                SellerOrdersScreen(
                    orders = orders,
                    isLoading = isLoading,
                    onBackClick = { findNavController().navigateUp() },
                    onUpdateStatus = { orderId, status ->
                        viewModel.updateStatus(orderId, status)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadOrders()

        viewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            when (success) {
                true -> Toast.makeText(requireContext(), "Status pengiriman diperbarui", Toast.LENGTH_SHORT).show()
                false -> Toast.makeText(requireContext(), "Gagal memperbarui status", Toast.LENGTH_LONG).show()
                null -> {}
            }
            if (success != null) viewModel.resetUpdateStatus()
        }
    }
}
