package com.example.janagroandroid.ui.seller

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class ManageProductsFragment : Fragment() {

    private val viewModel: ManageProductsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val products = viewModel.products.observeAsState(initial = emptyList()).value

                ManageProductsScreen(
                    products = products,
                    onBackClick = { findNavController().navigateUp() },
                    onAddProductClick = { findNavController().navigate(R.id.addProductFragment) },
                    onProductClick = { product ->
                        val bundle = bundleOf(
                            "productId" to product.id,
                            "productName" to product.name,
                            "productDescription" to product.description,
                            "productPrice" to product.price,
                            "productStock" to product.stock,
                            "productCategory" to product.category
                        )
                        findNavController().navigate(R.id.addProductFragment, bundle)
                    },
                    onEditClick = { product ->
                        val bundle = bundleOf(
                            "productId" to product.id,
                            "productName" to product.name,
                            "productDescription" to product.description,
                            "productPrice" to product.price,
                            "productStock" to product.stock,
                            "productCategory" to product.category
                        )
                        findNavController().navigate(R.id.addProductFragment, bundle)
                    },
                    onDeleteClick = { product ->
                        AlertDialog.Builder(requireContext())
                            .setTitle("Hapus Produk")
                            .setMessage("Apakah Anda yakin ingin menghapus ${product.name}?")
                            .setPositiveButton("Hapus") { _, _ ->
                                viewModel.deleteProduct(product.id)
                            }
                            .setNegativeButton("Batal", null)
                            .show()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadProducts()

        viewModel.deleteStatus.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                viewModel.loadProducts()
            } else if (success == false) {
                val errorMsg = viewModel.errorMessage.value
                val message = errorMsg ?: "Gagal menghapus produk"
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }
}
