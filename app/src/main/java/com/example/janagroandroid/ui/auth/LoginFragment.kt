package com.example.janagroandroid.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.ui.AppViewModelFactory
import com.example.janagroandroid.di.AppGraph
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private val vm: AuthViewModel by viewModels { AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext())) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by vm.uiState.collectAsState()
                LoginScreen(
                    uiState = uiState,
                    onLoginClick = { email, pass ->
                        handleLogin(email, pass)
                    },
                    onRegisterClick = {
                        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                    }
                )
            }
        }
    }

    private fun handleLogin(emailOrUsername: String, pass: String) {
        if (emailOrUsername.isEmpty() || pass.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            vm.login(emailOrUsername, pass) { isSuccess ->
                if (isSuccess) {
                    lifecycleScope.launch {
                        val repo = AppGraph.repository(requireContext())
                        val user = repo.getCurrentUser()
                        if (user?.role == "Admin") {
                            findNavController().navigate(R.id.action_loginFragment_to_adminHomeFragment)
                        } else {
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
