package com.example.janagroandroid.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

                ProfileScreen(
                    user = user,
                    onLogoutClick = {
                        viewModel.logout()
                    },
                    onImageSelected = {
                        Toast.makeText(requireContext(), "Foto profil berhasil diubah", Toast.LENGTH_SHORT).show()
                    },
                    onChatListClick = {
                        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToChatListFragment())
                    }
                )
            }
        }
    }
}
