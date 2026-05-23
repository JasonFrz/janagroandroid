package com.example.janagroandroid.ui.checkout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.janagroandroid.R
import com.example.janagroandroid.databinding.FragmentCheckoutBinding
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckoutViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCheckoutBinding.bind(view)

        val total = arguments?.getDouble("total") ?: 0.0
        binding.tvTotal.text = "Total: Rp $total"

        binding.btnPay.setOnClickListener {
            viewModel.checkout(total)
            // Menggunakan selectedItemId agar Bottom Navigation tersinkronisasi ke tab History
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav.selectedItemId = R.id.historyFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}