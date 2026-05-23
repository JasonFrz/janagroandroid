package com.example.janagroandroid.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SplashScreen(onNext = {
                    lifecycleScope.launch {
                        val role = viewModel.getRole()
                        if (role == "Admin") {
                            findNavController().navigate(R.id.action_splashFragment_to_adminHomeFragment)
                        } else {
                            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                        }
                    }
                })
            }
        }
    }
}
