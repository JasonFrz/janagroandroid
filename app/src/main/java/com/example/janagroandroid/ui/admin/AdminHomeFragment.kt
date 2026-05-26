package com.example.janagroandroid.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.data.remote.dto.AdminStats
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import com.example.janagroandroid.ui.auth.AuthViewModel
import com.example.janagroandroid.ui.theme.JanAgroTheme
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class AdminHomeFragment : Fragment() {
    private val authViewModel: AuthViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    private val adminViewModel: AdminHomeViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        adminViewModel.loadStats()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AdminHomeScreen(
                    viewModel = adminViewModel,
                    onLogout = {
                        lifecycleScope.launch {
                            authViewModel.logout()
                            findNavController().navigate(R.id.splashFragment)
                        }
                    },
                    onRefresh = { adminViewModel.loadStats() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AdminHomeViewModel,
    onLogout: () -> Unit,
    onRefresh: () -> Unit
) {
    val stats: AdminStats? by viewModel.stats.observeAsState()
    val isLoading: Boolean by viewModel.isLoading.observeAsState(false)

    JanAgroTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin Dashboard") },
                    actions = {
                        IconButton(onClick = onRefresh) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Summary Data", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                if (isLoading && stats == null) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    StatCard("Total Transaksi Hari Ini", formatRupiah(stats?.totalTransactionsToday ?: 0L))
                    StatCard("Jumlah User Aktif", (stats?.activeUsers ?: 0L).toString())
                    StatCard("Status Sistem", stats?.systemStatus ?: "-")
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 14.sp, color = androidx.compose.ui.graphics.Color.Gray)
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatRupiah(amount: Long): String {
    val nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("in-ID"))
    return "Rp ${nf.format(amount)}"
}