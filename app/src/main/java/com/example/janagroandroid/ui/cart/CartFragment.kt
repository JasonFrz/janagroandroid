package com.example.janagroandroid.ui.cart

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.janagroandroid.R
import com.example.janagroandroid.databinding.FragmentCartBinding
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.adapters.CartAdapter
import com.example.janagroandroid.ui.AppViewModelFactory

class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    private lateinit var adapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCartBinding.bind(view)

        adapter = CartAdapter(emptyList()) { item ->
            viewModel.delete(item)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.cart.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)

            val total = list.sumOf { it.price * it.qty }
            binding.tvTotal.text = "Total: Rp $total"

            binding.btnCheckout.setOnClickListener {
                findNavController().navigate(
                    R.id.checkoutFragment,
                    bundleOf("total" to total)
                )
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}