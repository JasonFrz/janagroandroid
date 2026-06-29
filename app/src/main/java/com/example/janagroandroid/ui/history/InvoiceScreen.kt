package com.example.janagroandroid.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.janagroandroid.R
import com.example.janagroandroid.data.remote.dto.OrderDto
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.material.icons.filled.Print
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    order: OrderDto?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Faktur Pembelian", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (order != null) {
                        IconButton(onClick = {
                            PrintHelper.printInvoice(context, order)
                        }) {
                            Icon(Icons.Filled.Print, contentDescription = "Print")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Memuat faktur...")
            }
            return@Scaffold
        }

        val formatCurrency = { amount: Double ->
            NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
                maximumFractionDigits = 0
            }.format(amount).replace("Rp", "Rp ")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Agrojan Logo",
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "AGROJAN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Info Section
                    Text("INFORMASI PEMBELI", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Nama: ${order.shippingAddress?.split("\n")?.firstOrNull() ?: "Pembeli"}", fontSize = 14.sp)
                    Text(text = "Alamat: ${order.shippingAddress ?: "-"}", fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("INFORMASI TOKO", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = order.merchant?.storeName ?: "Toko Tani", fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TANGGAL PESANAN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = order.createdAt.split("T").firstOrNull() ?: "-", fontSize = 14.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("METODE PEMBAYARAN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = order.paymentMethod ?: "Transfer Bank", fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("JASA KIRIM", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = order.courier ?: "Reguler", fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("RINCIAN PESANAN", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9))
                            .padding(8.dp)
                    ) {
                        Text("Produk", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Qty", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                        Text("Harga", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("Subtotal", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                    }

                    // Table Body
                    var productsSubtotal = 0.0
                    order.items?.forEach { item ->
                        val price = item.priceAtPurchaseDouble
                        val subtotal = price * item.quantity
                        productsSubtotal += subtotal

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.product?.name ?: "Produk", modifier = Modifier.weight(2f), fontSize = 12.sp)
                            Text("${item.quantity}", modifier = Modifier.weight(0.5f), fontSize = 12.sp, textAlign = TextAlign.Center)
                            Text(formatCurrency(price), modifier = Modifier.weight(1.5f), fontSize = 12.sp, textAlign = TextAlign.End)
                            Text(formatCurrency(subtotal), modifier = Modifier.weight(1.5f), fontSize = 12.sp, textAlign = TextAlign.End)
                        }
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Summary
                    val shippingCost = order.shippingCost?.toDoubleOrNull() ?: 0.0
                    val voucherDiscount = order.voucherDiscount?.toDoubleOrNull() ?: 0.0
                    val totalBeforeDiscount = productsSubtotal + shippingCost
                    val grandTotal = order.totalPriceDouble + shippingCost - voucherDiscount

                    InvoiceSummaryRow("Subtotal Produk", formatCurrency(productsSubtotal))
                    InvoiceSummaryRow("Ongkos Kirim", formatCurrency(shippingCost))
                    InvoiceSummaryRow("Total Sebelum Diskon", formatCurrency(totalBeforeDiscount))
                    InvoiceSummaryRow("Diskon Voucher", "-${formatCurrency(voucherDiscount)}", color = Color(0xFFD32F2F))
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Pembayaran", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(formatCurrency(grandTotal), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2E7D32))
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceSummaryRow(label: String, value: String, color: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, color = color, fontWeight = FontWeight.Medium)
    }
}
