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
import com.example.janagroandroid.data.remote.dto.VoucherDto
import com.example.janagroandroid.ui.AppViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale
import android.widget.AdapterView

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
        "GoPay" to "gopay",
        "ShopeePay" to "shopeepay",
        "BNI Virtual Account" to "bni_va",
        "BRI Virtual Account" to "bri_va",
        "Permata Virtual Account" to "permata_va",
        "CIMB Niaga Virtual Account" to "cimb_niaga_va"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentCheckoutBinding.bind(view)

        val selectedIds = arguments?.getLongArray("selectedIds") ?: longArrayOf()
        viewModel.loadInitialData(selectedIds)

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

        // Setting up the dynamic vouchers dropdown in observer

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

        viewModel.activeVouchers.observe(viewLifecycleOwner) { vouchers ->
            val voucherNames = mutableListOf("Pilih Voucher...")
            voucherNames.addAll(vouchers.map { it.code })

            binding.spinnerVoucher.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                voucherNames
            )
            
            binding.spinnerVoucher.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == 0) {
                        viewModel.selectVoucher(null)
                    } else {
                        viewModel.selectVoucher(vouchers[position - 1])
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    viewModel.selectVoucher(null)
                }
            }
        }

        viewModel.userAddress.observe(viewLifecycleOwner) { address ->
            if (!address.isNullOrEmpty() && binding.etShippingAddress.text.isNullOrEmpty()) {
                binding.etShippingAddress.setText(address)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CheckoutViewModel.CheckoutState.Loading -> setLoading(true)
                is CheckoutViewModel.CheckoutState.Success -> {
                    setLoading(false)
                    val orders = state.response?.data?.orders ?: emptyList()
                    val firstOrder = orders.firstOrNull()
                    
                    if (firstOrder != null) {
                        val paymentInfo = firstOrder.snapUrl
                        val qrData = firstOrder.qrString ?: if (paymentInfo != null && !paymentInfo.startsWith("http") && paymentInfo.length > 20) paymentInfo else null
                        val paymentMethod = firstOrder.paymentMethod ?: "Pembayaran"
                        
                        val builder = android.app.AlertDialog.Builder(requireContext())
                            .setTitle("Informasi Pembayaran")
                            .setCancelable(false)
                            .setPositiveButton("Ke Riwayat Pesanan") { _, _ ->
                                navigateToHistory()
                            }

                        if (qrData != null) {
                            val padding = (16 * resources.displayMetrics.density).toInt()
                            val layout = android.widget.LinearLayout(requireContext()).apply {
                                orientation = android.widget.LinearLayout.VERTICAL
                                setPadding(padding, padding, padding, padding)
                                gravity = android.view.Gravity.CENTER_HORIZONTAL
                            }
                            
                            val tvMethod = android.widget.TextView(requireContext()).apply {
                                text = "Metode: $paymentMethod"
                                setTypeface(null, android.graphics.Typeface.BOLD)
                                textSize = 16f
                                setTextColor(android.graphics.Color.BLACK)
                            }
                            layout.addView(tvMethod)
                            
                            val tvInstr = android.widget.TextView(requireContext()).apply {
                                text = "Silakan pindai QR Code di bawah ini untuk membayar:"
                                setPadding(0, padding / 2, 0, padding / 2)
                                setTextColor(android.graphics.Color.DKGRAY)
                            }
                            layout.addView(tvInstr)

                            val ivQr = android.widget.ImageView(requireContext()).apply {
                                layoutParams = android.widget.LinearLayout.LayoutParams(600, 600)
                            }
                            ivQr.load("https://api.qrserver.com/v1/create-qr-code/?size=500x500&data=$qrData") {
                                error(R.drawable.farmer)
                                placeholder(R.drawable.farmer)
                            }
                            layout.addView(ivQr)
                            
                            val tvCode = android.widget.TextView(requireContext()).apply {
                                text = "Kode: $qrData"
                                textSize = 10f
                                setPadding(0, padding / 2, 0, 0)
                                gravity = android.view.Gravity.CENTER
                                setTextColor(android.graphics.Color.GRAY)
                            }
                            layout.addView(tvCode)
                            
                            builder.setView(layout)
                        } else if (!paymentInfo.isNullOrEmpty() && paymentInfo.startsWith("http")) {
                            builder.setMessage("Metode: $paymentMethod\n\nSilakan klik 'Bayar Sekarang' untuk menyelesaikan pembayaran.")
                            builder.setNeutralButton("Bayar Sekarang") { _, _ ->
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(paymentInfo))
                                    startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), "Gagal membuka link pembayaran", Toast.LENGTH_SHORT).show()
                                }
                                navigateToHistory()
                            }
                        } else {
                            builder.setMessage("Metode: $paymentMethod\n\nKode/VA: ${paymentInfo ?: "-"}\n\nSilakan selesaikan pembayaran Anda.")
                        }
                        
                        builder.show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            state.response?.message ?: "Checkout berhasil",
                            Toast.LENGTH_LONG
                        ).show()
                        navigateToHistory()
                    }
                    viewModel.resetState()
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

    private fun navigateToHistory() {
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottomNav)
            .selectedItemId = R.id.historyFragment
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
