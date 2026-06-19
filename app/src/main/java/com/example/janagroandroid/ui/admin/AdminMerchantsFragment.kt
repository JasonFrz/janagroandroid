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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.janagroandroid.ui.theme.JanAgroTheme

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMerchantsScreen(viewModel: AdminMerchantsViewModel) {
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
                AdminMerchantsTopBar(onRefresh = { viewModel.loadPending() })
            }
        ) { padding ->
            AdminMerchantsContent(
                padding = padding,
                merchants = merchants,
                isLoading = isLoading,
                onApprove = { id -> viewModel.updateStatus(id, "Approved") },
                onReject = { id -> viewModel.updateStatus(id, "Rejected") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminMerchantsTopBar(onRefresh: () -> Unit) {
    TopAppBar(
        title = { Text("Persetujuan Merchant") },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
    )
}

@Composable
private fun AdminMerchantsContent(
    padding: androidx.compose.foundation.layout.PaddingValues,
    merchants: List<MerchantDto>,
    isLoading: Boolean,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Menunggu persetujuan: ${merchants.size}",
            fontSize = 13.sp,
            color = Color.Gray
        )
        when {
            isLoading && merchants.isEmpty() -> {
                LoadingBox()
            }
            merchants.isEmpty() -> {
                EmptyBox()
            }
            else -> {
                MerchantList(
                    merchants = merchants,
                    onApprove = onApprove,
                    onReject = onReject
                )
            }
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(32.dp), contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyBox() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(32.dp), contentAlignment = Alignment.Center
    ) {
        Text("Tidak ada merchant yang menunggu persetujuan", color = Color.Gray)
    }
}

@Composable
private fun MerchantList(
    merchants: List<MerchantDto>,
    onApprove: (Long) -> Unit,
    onReject: (Long) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(merchants) { merchant ->
            MerchantCard(
                merchant = merchant,
                onApprove = { merchant.id?.let { onApprove(it) } },
                onReject = { merchant.id?.let { onReject(it) } }
            )
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
            if (!merchant.city.isNullOrBlank()) {
                Text(text = merchant.city, fontSize = 13.sp, color = Color.Gray)
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
                    modifier = Modifier.weight(1f),
                    enabled = merchant.id != null
                ) { Text("Tolak") }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    enabled = merchant.id != null
                ) { Text("Setujui") }
            }
        }
    }
}
