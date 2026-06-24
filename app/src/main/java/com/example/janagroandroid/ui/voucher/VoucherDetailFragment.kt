package com.example.janagroandroid.ui.voucher

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class VoucherDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val code = arguments?.getString("code") ?: ""
        val description = arguments?.getString("description")
        val discountValue = arguments?.getString("discountValue") ?: "0"
        val discountType = arguments?.getString("discountType") ?: "Fixed_Amount"
        val minPurchase = arguments?.getString("minPurchase") ?: "0"
        val endDate = arguments?.getString("endDate")

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                VoucherDetailScreen(
                    code = code,
                    description = description,
                    discountValue = discountValue,
                    discountType = discountType,
                    minPurchase = minPurchase,
                    endDate = endDate,
                    onBackClick = { findNavController().popBackStack() },
                    onCopyCode = { codeToCopy ->
                        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Voucher Code", codeToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(requireContext(), "Kode voucher disalin!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
