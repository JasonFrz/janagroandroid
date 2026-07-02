package com.example.janagroandroid.ui.product

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

        // Observe add-to-cart result and show Toast
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
                val productDetail by viewModel.product.observeAsState()
                val reviews by viewModel.reviews.observeAsState(emptyList())
                val aiTips by viewModel.aiTips.observeAsState()
                val isAiLoading by viewModel.isAiLoading.observeAsState(false)
                val isLoading by viewModel.isLoading.observeAsState(false)
                val activeNegotiation by viewModel.activeNegotiation.observeAsState()

                ProductDetailScreen(
                    id = id,
                    name = productDetail?.name ?: initialName,
                    price = productDetail?.price ?: initialPrice,
                    imageUrl = productDetail?.imageUrl ?: initialImageUrl,
                    description = productDetail?.description ?: initialDescription,
                    isLoading = isLoading && productDetail == null,
                    merchantId = productDetail?.merchant_id ?: initialMerchantId,
                    merchantUserId = productDetail?.merchantUserId ?: initialMerchantUserId,
                    merchantName = productDetail?.merchant_name ?: initialMerchantName,
                    merchantAddress = productDetail?.merchant_city ?: initialMerchantCity,
                    merchantProfileUrl = productDetail?.merchantProfileUrl ?: "",
                    category = productDetail?.category ?: "",
                    reviews = reviews,
                    aiTips = aiTips,
                    isAiLoading = isAiLoading,
                    activeNegotiation = activeNegotiation,
                    onAiTipsClick = { viewModel.fetchAiTips(id) },
                    onBackClick = { findNavController().popBackStack() },
                    onAddToCartClick = { qty ->
                        viewModel.addToCart(id, qty)
                    },
                    onBuyNowClick = {
                        viewModel.addToCart(id, 1)
                        findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
                    },
                    onCartClick = {
                        findNavController().navigate(R.id.action_productDetailFragment_to_cartFragment)
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
                            Toast.makeText(requireContext(), "ID Penjual tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    },
                    wholesaleMinQty = productDetail?.wholesaleMinQty,
                    wholesalePrice = productDetail?.wholesalePrice,
                    onNegoSubmit = { negoPrice ->
                        val mUserId = productDetail?.merchantUserId ?: initialMerchantUserId
                        val mId = productDetail?.merchant_id ?: initialMerchantId
                        val finalPartnerId = if (mUserId != 0L) mUserId else mId
                        if (finalPartnerId != 0L) {
                            val bundle = bundleOf(
                                "partnerId" to finalPartnerId,
                                "partnerName" to (productDetail?.merchant_name ?: initialMerchantName),
                                "negoProductId" to id,
                                "negoPrice" to negoPrice
                            )
                            findNavController().navigate(R.id.action_productDetailFragment_to_chatFragment, bundle)
                        } else {
                            Toast.makeText(requireContext(), "ID Penjual tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

