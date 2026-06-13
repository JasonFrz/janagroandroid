package com.example.janagroandroid.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val user by viewModel.user.observeAsState()
                val updateStatus by viewModel.profileUpdateStatus.observeAsState()
                val logoutSuccess by viewModel.logoutSuccess.observeAsState(false)

                // Navigasi setelah logout berhasil
                LaunchedEffect(logoutSuccess) {
                    if (logoutSuccess) {
                        findNavController().navigate(
                            R.id.loginFragment,
                            null,
                            NavOptions.Builder()
                                .setPopUpTo(R.id.nav_graph, true)
                                .build()
                        )
                    }
                }

                // Memberikan feedback Toast saat status update berubah
                val currentStatus = updateStatus
                LaunchedEffect(currentStatus) {
                    when (currentStatus) {
                        is ProfileViewModel.ProfileUpdateStatus.Success -> {
                            Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        }
                        is ProfileViewModel.ProfileUpdateStatus.SuccessMessage -> {
                            Toast.makeText(requireContext(), currentStatus.message, Toast.LENGTH_SHORT).show()
                        }
                        is ProfileViewModel.ProfileUpdateStatus.Error -> {
                            Toast.makeText(requireContext(), currentStatus.message, Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }

                ProfileScreen(
                    user = user,
                    onLogoutClick = {
                        viewModel.logout()
                    },
                    onImageSelected = { uri ->
                        // Memanggil fungsi upload di ViewModel
                        viewModel.updateProfilePicture(uri)
                    },
                    onUpdateProfile = { name, phone ->
                        // Memanggil fungsi update profil di ViewModel
                        viewModel.updateProfileInfo(name, phone)
                    },
                    onChatListClick = {
                        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChatListFragment())
                    },
                    onRequestBecomeSeller = { storeName, description ->
                        viewModel.requestBecomeSeller(storeName, description)
                    }
                )
            }
        }
    }
}
