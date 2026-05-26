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

class RegisterFragment : Fragment() {
    private val vm: AuthViewModel by viewModels { AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext())) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by vm.uiState.collectAsState()
                RegisterScreen(
                    uiState = uiState,
                    onRegisterClick = { name, email, pass, phone, role, confirmPass ->
                        handleRegistration(name, email, pass, phone, role, confirmPass)
                    },
                    onLoginClick = {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                )
            }
        }
    }

    private fun handleRegistration(name: String, email: String, pass: String, phone: String, role: String, confirmPass: String) {
        // Basic Validation
        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (pass != confirmPass) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            vm.register(name, email, pass, phone, role, confirmPass) { isSuccess ->
                if (isSuccess) {
                    Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                } else {
                    Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
