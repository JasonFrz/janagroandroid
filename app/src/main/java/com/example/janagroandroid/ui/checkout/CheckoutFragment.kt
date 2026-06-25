package com.example.janagroandroid.ui.checkout

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.janagroandroid.R
import com.example.janagroandroid.databinding.FragmentCheckoutBinding
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckoutViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    // Courier values accepted by the backend validator.
    private val courierOptions = listOf("JNE", "TIKI", "POS", "GoSend", "GrabExpress")

    // Display label -> backend payment_type value.
    private val paymentOptions = listOf(
        "QRIS" to "qris",
        "Virtual Account BNI" to "bni_va",
        "Virtual Account BRI" to "bri_va",
        "Virtual Account Permata" to "permata_va"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCheckoutBinding.bind(view)

        val total = arguments?.getDouble("total") ?: 0.0
        binding.tvTotal.text = "Total: Rp${String.format(Locale.GERMANY, "%,.0f", total)}"

        binding.spinnerCourier.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            courierOptions
        )
        binding.spinnerPayment.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            paymentOptions.map { it.first }
        )

        binding.btnPay.setOnClickListener { submitCheckout() }

        observeState()
    }

    private fun submitCheckout() {
        val address = binding.etShippingAddress.text?.toString()?.trim().orEmpty()
        if (address.length < 10) {
            binding.etShippingAddress.error = "Alamat minimal 10 karakter"
            binding.etShippingAddress.requestFocus()
            return
        }

        val courier = courierOptions.getOrNull(binding.spinnerCourier.selectedItemPosition)
        val paymentType = paymentOptions.getOrNull(binding.spinnerPayment.selectedItemPosition)?.second
        val voucherCode = binding.etVoucherCode.text?.toString()?.trim()?.ifBlank { null }

        viewModel.checkout(
            shippingAddress = address,
            courier = courier,
            voucherCode = voucherCode,
            paymentType = paymentType
        )
    }

    private fun observeState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CheckoutViewModel.CheckoutState.Loading -> setLoading(true)
                is CheckoutViewModel.CheckoutState.Success -> {
                    setLoading(false)
                    Toast.makeText(
                        requireContext(),
                        state.message ?: "Checkout berhasil",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetState()
                    // Sync the bottom navigation to the History tab.
                    requireActivity()
                        .findViewById<BottomNavigationView>(R.id.bottomNav)
                        .selectedItemId = R.id.historyFragment
                }
                is CheckoutViewModel.CheckoutState.Error -> {
                    setLoading(false)
                    Toast.makeText(
                        requireContext(),
                        state.message ?: "Checkout gagal. Silakan coba lagi.",
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetState()
                }
                else -> setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnPay.isEnabled = !loading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
