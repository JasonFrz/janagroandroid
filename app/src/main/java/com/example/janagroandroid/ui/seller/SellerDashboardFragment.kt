package com.example.janagroandroid.ui.seller

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.janagroandroid.R
import com.example.janagroandroid.databinding.FragmentSellerDashboardBinding
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import com.example.janagroandroid.ui.adapters.ProductAdapter

class SellerDashboardFragment : Fragment(R.layout.fragment_seller_dashboard) {

    private var _binding: FragmentSellerDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SellerDashboardViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    private lateinit var adapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSellerDashboardBinding.bind(view)

        adapter = ProductAdapter(emptyList(), onClick = { })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.sellerProducts.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}