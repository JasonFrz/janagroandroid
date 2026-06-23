package com.example.janagroandroid.ui.product

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

class ProductDetailFragment : Fragment() {

    private val viewModel: ProductDetailViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val id = arguments?.getLong("id") ?: 0L
        val initialName = arguments?.getString("name").orEmpty()
        val initialPrice = arguments?.getFloat("price")?.toDouble() ?: 0.0
        val initialImageUrl = arguments?.getString("imageUrl").orEmpty()
        val initialDescription = arguments?.getString("description").orEmpty()
        val initialMerchantId = arguments?.getLong("merchantId") ?: 0L
        val initialMerchantUserId = arguments?.getLong("merchantUserId") ?: 0L
        val initialMerchantName = arguments?.getString("merchantName") ?: "Agrojan Store"
        val initialMerchantCity = arguments?.getString("merchantCity") ?: "Surabaya, Jawa Timur"

        viewModel.fetchProductDetail(id)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val productDetail by viewModel.product.observeAsState()
                val reviews by viewModel.reviews.observeAsState(emptyList())
                
                ProductDetailScreen(
                    id = id,
                    name = productDetail?.name ?: initialName,
                    price = productDetail?.price ?: initialPrice,
                    imageUrl = productDetail?.imageUrl ?: initialImageUrl,
                    description = productDetail?.description ?: initialDescription,
                    merchantId = productDetail?.merchant_id ?: initialMerchantId,
                    merchantUserId = productDetail?.merchantUserId ?: initialMerchantUserId,
                    merchantName = productDetail?.merchant_name ?: initialMerchantName,
                    merchantAddress = productDetail?.merchant_city ?: initialMerchantCity,
                    category = productDetail?.category ?: "",
                    reviews = reviews,
                    onBackClick = { findNavController().popBackStack() },
                    onAddToCartClick = { qty ->
                        viewModel.addToCart(id, initialName, initialPrice, initialImageUrl, qty)
                    },
                    onMerchantClick = { mId ->
                        if (mId != 0L) {
                            val bundle = bundleOf("merchantId" to mId)
                            findNavController().navigate(R.id.action_productDetailFragment_to_merchantDetailFragment, bundle)
                        }
                    },
                    onChatClick = { partnerId, partnerName ->
                        if (partnerId != 0L) {
                            val bundle = bundleOf(
                                "partnerId" to partnerId,
                                "partnerName" to partnerName
                            )
                            findNavController().navigate(R.id.action_productDetailFragment_to_chatFragment, bundle)
                        } else {
                            android.widget.Toast.makeText(requireContext(), "ID Penjual tidak ditemukan", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}
