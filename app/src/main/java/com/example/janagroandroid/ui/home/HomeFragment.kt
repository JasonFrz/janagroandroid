package com.example.janagroandroid.ui.home

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
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val products by viewModel.products.observeAsState(emptyList())
                val recentlyListed by viewModel.recentlyListed.observeAsState(emptyList())
                val categories by viewModel.categories.observeAsState(emptyList())
                val selectedCategory by viewModel.selectedCategory.observeAsState()
                val topMerchants by viewModel.topMerchants.observeAsState(emptyList())
                val activeVouchers by viewModel.activeVouchers.observeAsState(emptyList())
                val user by viewModel.currentUser.observeAsState()

                HomeScreen(
                    user = user,
                    products = products,
                    recentlyListed = recentlyListed,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    topMerchants = topMerchants,
                    activeVouchers = activeVouchers,
                    onProfileClick = {
                        if (user != null) {
                            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
                            bottomNav.selectedItemId = R.id.profileFragment
                        } else {
                            findNavController().navigate(R.id.loginFragment)
                        }
                    },
                    onCategoryClick = { categoryName ->
                        viewModel.selectCategory(categoryName)
                    },
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
                    onVoucherClick = { voucher ->
                        val bundle = bundleOf(
                            "voucherId" to voucher.id,
                            "code" to voucher.code,
                            "description" to voucher.description,
                            "discountValue" to voucher.discountValue,
                            "discountType" to voucher.discountType,
                            "minPurchase" to voucher.minPurchase,
                            "endDate" to voucher.endDate
                        )
                        findNavController().navigate(R.id.voucherDetailFragment, bundle)
                    },
                    onNotificationClick = {
                        Toast.makeText(requireContext(), "Notifikasi", Toast.LENGTH_SHORT).show()
                    },
                    onCartClick = {
                        findNavController().navigate(R.id.cartFragment)
                    },
                    onSearchSubmit = { query ->
                        val bundle = bundleOf("query" to query)
                        findNavController().navigate(R.id.exploreFragment, bundle)
                    },
                    onViewAllRecentlyListed = {
                        // Navigate to Explore with "All" to show all products sorted by newest
                        val bundle = bundleOf("category" to "All")
                        findNavController().navigate(R.id.exploreFragment, bundle)
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
