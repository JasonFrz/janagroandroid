package com.example.janagroandroid.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.ui.theme.JanAgroTheme

import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class InvoiceFragment : Fragment() {

    private val viewModel: HistoryViewModel by activityViewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val orderId = arguments?.getLong("orderId") ?: 0L

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                JanAgroTheme {
                    val order = viewModel.orders.value?.find { it.id == orderId }
                    InvoiceScreen(
                        order = order,
                        onBackClick = { findNavController().popBackStack() }
                    )
                }
            }
        }
    }
}
