package com.example.janagroandroid.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.janagroandroid.data.remote.dto.OrderDto
import com.example.janagroandroid.ui.theme.JanAgroTheme
import java.util.Locale

private val primaryGreen = Color(0xFF2E7D32)

private fun statusLabel(status: String): String = when (status) {
    "Pending_Payment" -> "Menunggu Pembayaran"
    "Paid" -> "Perlu Diproses"
    "Packed" -> "Diproses"
    "Shipped" -> "Dikirim"
    "Completed" -> "Selesai"
    "Cancelled" -> "Dibatalkan"
    else -> status
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrdersScreen(
    orders: List<OrderDto>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onUpdateStatus: (orderId: Long, status: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredOrders = remember(orders, searchQuery) {
        if (searchQuery.isBlank()) {
            orders
        } else {
            val q = searchQuery.trim()
            orders.filter { order ->
                order.id.toString().contains(q, ignoreCase = true) ||
                    statusLabel(order.status).contains(q, ignoreCase = true) ||
                    order.customer?.name?.contains(q, ignoreCase = true) == true ||
                    order.shippingAddress?.contains(q, ignoreCase = true) == true ||
                    order.items?.any { it.product?.name?.contains(q, ignoreCase = true) == true } == true
            }
        }
    }

    JanAgroTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Kelola Transaksi", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = primaryGreen)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF2F2F2))
            ) {
                // Dynamic search bar (filters live as the seller types)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    placeholder = { Text("Cari pesanan, pembeli, produk, status...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryGreen) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryGreen,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        isLoading && orders.isEmpty() -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = primaryGreen
                            )
                        }
                        filteredOrders.isEmpty() -> {
                            Text(
                                text = if (orders.isEmpty()) "Belum ada transaksi" else "Tidak ada transaksi yang cocok",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 24.dp, top = 4.dp)
                            ) {
                                items(filteredOrders, key = { it.id }) { order ->
                                    SellerOrderCard(order = order, onUpdateStatus = onUpdateStatus)
                                }
                            }
                        }
                    }

                    if (isLoading && orders.isNotEmpty()) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter),
                            color = primaryGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerOrderCard(
    order: OrderDto,
    onUpdateStatus: (orderId: Long, status: String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pesanan #${order.id}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                StatusChip(order.status)
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = "Pembeli: ${order.customer?.name ?: "-"}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            order.shippingAddress?.takeIf { it.isNotBlank() }?.let {
                Text(text = "Alamat: $it", fontSize = 12.sp, color = Color.Gray, maxLines = 2)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF2F2F2))

            // Product lines
            order.items.orEmpty().forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.product?.name ?: "Produk"} x${item.quantity}",
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Rp${String.format(Locale.GERMANY, "%,.0f", item.priceAtPurchaseDouble)}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF2F2F2))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total", fontSize = 13.sp, color = Color.Gray)
                Text(
                    text = "Rp${String.format(Locale.GERMANY, "%,.0f", order.totalPriceDouble)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryGreen
                )
            }

            // Action button to advance shipping status
            val action: Pair<String, String>? = when (order.status) {
                "Paid" -> "Proses Pesanan" to "Packed"
                "Packed" -> "Kirim Pesanan" to "Shipped"
                "Shipped" -> "Selesaikan Pesanan" to "Completed"
                else -> null
            }

            if (action != null) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { onUpdateStatus(order.id, action.second) },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                ) {
                    Text(action.first, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (bg, fg) = when (status) {
        "Pending_Payment" -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
        "Paid" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        "Packed" -> Color(0xFFEDE7F6) to Color(0xFF5E35B1)
        "Shipped" -> Color(0xFFE8F5E9) to primaryGreen
        "Completed" -> Color(0xFFE0F2F1) to Color(0xFF00695C)
        "Cancelled" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
        else -> Color(0xFFEEEEEE) to Color.DarkGray
    }
    Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
        Text(
            text = statusLabel(status),
            color = fg,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
