package com.example.janagroandroid.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
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

        val selectedIds = arguments?.getLongArray("selectedIds") ?: longArrayOf()
        viewModel.loadItems(selectedIds)

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

        binding.btnUseVoucher1.setOnClickListener {
            viewModel.toggleVoucher("GRATISONGKIR")
        }

        binding.btnUseVoucher2.setOnClickListener {
            viewModel.toggleVoucher("DISKON10")
        }

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

        viewModel.checkout(
            shippingAddress = address,
            courier = courier,
            paymentType = paymentType
        )
    }

    private fun observeState() {
        viewModel.items.observe(viewLifecycleOwner) { items ->
            binding.llProductList.removeAllViews()
            val inflater = LayoutInflater.from(requireContext())
            for (item in items) {
                val itemView = inflater.inflate(R.layout.item_checkout_product, binding.llProductList, false)
                val tvMerchantName = itemView.findViewById<TextView>(R.id.tvMerchantName)
                val ivProductImage = itemView.findViewById<ImageView>(R.id.ivProductImage)
                val tvProductName = itemView.findViewById<TextView>(R.id.tvProductName)
                val tvProductPriceQty = itemView.findViewById<TextView>(R.id.tvProductPriceQty)

                tvMerchantName.text = item.merchantName
                tvProductName.text = item.productName
                tvProductPriceQty.text = "Rp${String.format(Locale.GERMANY, "%,.0f", item.price)} x ${item.qty}"

                item.imageUrl?.replace("http://", "https://")?.let { url ->
                    ivProductImage.load(url) {
                        crossfade(true)
                        error(R.drawable.farmer)
                        placeholder(R.drawable.farmer)
                    }
                } ?: run {
                    ivProductImage.setImageResource(R.drawable.farmer)
                }

                binding.llProductList.addView(itemView)
            }
        }

        viewModel.subtotal.observe(viewLifecycleOwner) { subtotal ->
            binding.tvSubtotal.text = "Rp${String.format(Locale.GERMANY, "%,.0f", subtotal)}"
        }

        viewModel.discount.observe(viewLifecycleOwner) { discount ->
            binding.tvDiscount.text = "-Rp${String.format(Locale.GERMANY, "%,.0f", discount)}"
        }

        viewModel.finalTotal.observe(viewLifecycleOwner) { total ->
            binding.tvTotal.text = "Rp${String.format(Locale.GERMANY, "%,.0f", total)}"
        }

        viewModel.selectedVoucher.observe(viewLifecycleOwner) { voucher ->
            when (voucher) {
                "GRATISONGKIR" -> {
                    binding.btnUseVoucher1.text = "Batalkan"
                    binding.btnUseVoucher2.text = "Gunakan"
                }
                "DISKON10" -> {
                    binding.btnUseVoucher1.text = "Gunakan"
                    binding.btnUseVoucher2.text = "Batalkan"
                }
                else -> {
                    binding.btnUseVoucher1.text = "Gunakan"
                    binding.btnUseVoucher2.text = "Gunakan"
                }
            }
        }

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
