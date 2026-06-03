package com.example.janagroandroid.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class CartFragment : Fragment() {

    private val viewModel: CartViewModel by viewModels {
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
                val cartItems by viewModel.cart.observeAsState(emptyList())

                CartScreen(
                    cartItems = cartItems,
                    onBackClick = { findNavController().navigateUp() },
                    onDeleteClick = { id -> viewModel.deleteCart(id) },
                    onUpdateQty = { id, qty -> viewModel.updateQuantity(id, qty) },
                    onCheckoutClick = { total ->
                        findNavController().navigate(
                            R.id.checkoutFragment,
                            bundleOf("total" to total)
                        )
                    },
                    onProductClick = { id ->
                        // Navigasi ke detail produk jika ID tersedia (saat ini dummy 0L)
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchCart()
    }
}
