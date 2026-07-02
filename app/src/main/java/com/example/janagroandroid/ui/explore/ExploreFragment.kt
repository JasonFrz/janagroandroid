package com.example.janagroandroid.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.janagroandroid.ui.home.HomeViewModel

class ExploreFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        val sessionManager = com.example.janagroandroid.data.local.SessionManager(requireContext())
        AppViewModelFactory(
            app = requireActivity().application, 
            repo = AppGraph.repository(requireContext()),
            socketManager = com.example.janagroandroid.data.remote.SocketManager(sessionManager)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val initialSearch = arguments?.getString("query") ?: ""
        val initialCategory = arguments?.getString("category")

        // Observe add-to-cart result for Toast feedback
        viewModel.addToCartResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            val msg = if (result.isSuccess) {
                result.getOrNull() ?: "Produk ditambahkan ke keranjang"
            } else {
                result.exceptionOrNull()?.message ?: "Gagal menambahkan ke keranjang"
            }
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            viewModel.clearAddToCartResult()
        }
        
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val products by viewModel.products.observeAsState(emptyList())
                val categories by viewModel.categories.observeAsState(emptyList())
                val user by viewModel.currentUser.observeAsState()
                val isLoading by viewModel.isLoading.observeAsState(false)

                ExploreScreen(
                    initialQuery = initialSearch,
                    initialCategory = initialCategory,
                    products = products,
                    categories = categories,
                    isLoading = isLoading,
                    onBackClick = { findNavController().navigateUp() },
                    onProductClick = { product ->
                        if (user != null) {
                            val bundle = bundleOf(
                                "id" to product.id,
                                "name" to product.name,
                                "price" to product.price.toFloat(),
                                "imageUrl" to product.imageUrl,
                                "description" to product.description,
                                "merchantId" to product.merchant_id,
                                "merchantUserId" to product.merchantUserId,
                                "merchantName" to product.merchant_name,
                                "merchantCity" to product.merchant_city,
                                "category" to product.category
                            )
                            findNavController().navigate(R.id.productDetailFragment, bundle)
                        } else {
                            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.loginFragment)
                        }
                    },
                    onAddToCartClick = { product ->
                        if (user != null) {
                            viewModel.addToCart(product.id)
                        } else {
                            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.loginFragment)
                        }
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshRemote()
    }
}
