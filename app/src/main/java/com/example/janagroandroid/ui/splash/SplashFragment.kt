package com.example.janagroandroid.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R

class SplashFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SplashScreen(onNext = {
                    // Karena homeFragment sekarang adalah startDestination, 
                    // kita cukup pop splashFragment untuk kembali ke home
                    if (!findNavController().popBackStack()) {
                        findNavController().navigate(R.id.homeFragment)
                    }
                })
            }
        }
    }
}
