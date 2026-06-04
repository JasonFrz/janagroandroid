package com.example.janagroandroid.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.janagroandroid.data.remote.dto.MerchantDto
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import androidx.compose.material3.CircularProgressIndicator
import com.example.janagroandroid.ui.theme.JanAgroTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items

class AdminMerchantsFragment : Fragment() {

    private val viewModel: AdminMerchantsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel.loadPending()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AdminMerchantsScreen(viewModel = viewModel)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AdminMerchantsScreen(viewModel: AdminMerchantsViewModel){
        val merchants: List<MerchantDto> by viewModel.merchants.observeAsState(emptyList())
        val isLoading: Boolean by viewModel.isLoading.observeAsState(false)
        val message: String? by viewModel.message.observeAsState(null)
        val context = LocalContext.current

        LaunchedEffect(message) {
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        JanAgroTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Persetujuan Merchant")},
                        actions = {
                            IconButton(onClick = {viewModel.loadPending()}) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Menunggu persetujuan: ${merchants.size}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    when {
                        isLoading && merchants.isEmpty() -> {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center){
                                CircularProgressIndicator()
                            }
                        }
                        merchants.isEmpty() -> {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center){
                                Text("Tidak ada merchant yang menunggu persetujuan", color = Color.Gray)
                            }
                        }
                        else -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(merchants) { merchant ->
                                    MerchantCard(
                                        merchant = merchant,
                                        onApprove = { viewModel.updateStatus(merchant.id, "Approved") },
                                        onReject = { viewModel.updateStatus(merchant.id, "Rejected") }
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MerchantCard(
        merchant: MerchantDto,
        onApprove: () -> Unit,
        onReject: () -> Unit
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = merchant.storeName ?: "(Tanpa nama toko)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                merchant.owner?.let {
                    Text(text = "Pemilik: ${it.name}", fontSize = 13.sp, color = Color.Gray)
                }
                if (!merchant.address.isNullOrBlank()) {
                    Text(text = merchant.address, fontSize = 13.sp, color = Color.Gray)
                }
                if (!merchant.description.isNullOrBlank()) {
                    Text(text = merchant.description, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) { Text("Tolak") }

                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f)
                    ) { Text("Setujui") }
                }
            }
        }
    }

}