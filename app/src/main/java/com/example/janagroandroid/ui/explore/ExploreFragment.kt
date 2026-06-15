package com.example.janagroandroid.ui.explore

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
import com.example.janagroandroid.ui.home.HomeViewModel

class ExploreFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val initialSearch = arguments?.getString("query") ?: ""
        val initialCategory = arguments?.getString("category")
        
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val products by viewModel.products.observeAsState(emptyList())
                val categories by viewModel.categories.observeAsState(emptyList())

                ExploreScreen(
                    initialQuery = initialSearch,
                    initialCategory = initialCategory,
                    products = products,
                    categories = categories,
                    onBackClick = { findNavController().navigateUp() },
                    onProductClick = { product ->
                        val bundle = bundleOf(
                            "id" to product.id,
                            "name" to product.name,
                            "price" to product.price.toFloat(),
                            "imageUrl" to product.imageUrl,
                            "description" to product.description,
                            "merchantName" to product.merchant_name,
                            "category" to product.category
                        )
                        findNavController().navigate(R.id.productDetailFragment, bundle)
                    }
                )
            }
        }
    }
}
