package com.example.janagroandroid.ui.merchant

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

class MerchantDetailFragment : Fragment() {

    private val viewModel: MerchantDetailViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val merchantId = arguments?.getLong("merchantId") ?: 0L
        viewModel.loadMerchantData(merchantId)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val merchant by viewModel.merchant.observeAsState()
                val products by viewModel.products.observeAsState(emptyList())
                val isLoading by viewModel.isLoading.observeAsState(false)
                val searchQuery by viewModel.searchQuery.observeAsState("")

                MerchantDetailScreen(
                    merchant = merchant,
                    products = products,
                    isLoading = isLoading,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                    onBackClick = { findNavController().popBackStack() },
                    onProductClick = { id, name, price, imageUrl, description, mName, mCity ->
                        val bundle = bundleOf(
                            "id" to id,
                            "name" to name,
                            "price" to price.toFloat(),
                            "imageUrl" to imageUrl,
                            "description" to description,
                            "merchantId" to merchantId,
                            "merchantName" to mName,
                            "merchantCity" to mCity
                        )
                        findNavController().navigate(R.id.action_merchantDetailFragment_to_productDetailFragment, bundle)
                    }
                )
            }
        }
    }
}
