package com.example.janagroandroid.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class ProductDetailFragment : Fragment() {

    private val viewModel: ProductDetailViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val id = arguments?.getLong("id") ?: 0L
        val initialName = arguments?.getString("name").orEmpty()
        val initialPrice = arguments?.getDouble("price") ?: 0.0
        val initialImageUrl = arguments?.getString("imageUrl").orEmpty()
        val initialDescription = arguments?.getString("description").orEmpty()

        viewModel.loadProductDetail(id)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val productDetail by viewModel.product.collectAsStateWithLifecycle()
                
                ProductDetailScreen(
                    id = id,
                    name = productDetail?.name ?: initialName,
                    price = productDetail?.price ?: initialPrice,
                    imageUrl = productDetail?.imageUrl ?: initialImageUrl,
                    description = productDetail?.description ?: initialDescription,
                    onBackClick = { findNavController().popBackStack() },
                    onAddToCartClick = { qty ->
                        productDetail?.let {
                            viewModel.addToCart(it, qty)
                            findNavController().popBackStack()
                        }
                    }
                )
            }
        }
    }
}
