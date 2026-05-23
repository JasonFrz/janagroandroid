package com.example.janagroandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import com.example.janagroandroid.ui.auth.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }
    
    private val authViewModel: AuthViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val products by viewModel.products.observeAsState(emptyList())
                val user by viewModel.currentUser.observeAsState()

                HomeScreen(
                    user = user,
                    products = products,
                    onProfileClick = {
                        if (user != null) {
                            // Menggunakan selectedItemId agar state BottomNav tersinkronisasi
                            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
                            bottomNav.selectedItemId = R.id.profileFragment
                        } else {
                            findNavController().navigate(R.id.loginFragment)
                        }
                    },
                    onProductClick = { product ->
                        if (user != null) {
                            val bundle = bundleOf(
                                "id" to product.id,
                                "name" to product.name,
                                "price" to product.price,
                                "stock" to product.stock,
                                "imageUrl" to product.imageUrl,
                                "description" to product.description,
                                "category" to product.category
                            )
                            findNavController().navigate(R.id.productDetailFragment, bundle)
                        } else {
                            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.loginFragment)
                        }
                    },
                    onSearchClick = {
                        // Handle search
                    },
                    onLogoutClick = {
                        lifecycleScope.launch {
                            authViewModel.logout()
                            // Navigasi ke splash akan ditangani oleh observer di MainActivity
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
