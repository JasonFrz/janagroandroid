package com.example.janagroandroid.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.janagroandroid.R
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.ui.theme.JanAgroTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartEntity>,
    onBackClick: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    onUpdateQty: (Long, Int) -> Unit,
    onCheckoutClick: (Double) -> Unit,
    onProductClick: (Long) -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }

    // Update selectedIds when cartItems change if they are not tracked yet
    LaunchedEffect(cartItems) {
        if (selectedIds.isEmpty() && cartItems.isNotEmpty()) {
            selectedIds = cartItems.map { it.id }.toSet()
        } else {
            val validIds = cartItems.map { it.id }.toSet()
            selectedIds = selectedIds.intersect(validIds)
        }
    }

    val selectedItems = cartItems.filter { it.id in selectedIds }
    val totalPayment = selectedItems.sumOf { it.price * it.qty }
    val isAllSelected = cartItems.isNotEmpty() && selectedIds.size == cartItems.size

    JanAgroTheme {
        Scaffold(
            topBar = {
                Surface(
                    shadowElevation = 0.dp,
                    color = Color.White,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Keranjang",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 18.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Search */ }) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                    )
                }
            },
            bottomBar = {
                CartBottomBar(
                    totalPrice = totalPayment,
                    itemCount = selectedItems.size,
                    onCheckoutClick = { onCheckoutClick(totalPayment) }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF8F5F2)),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Select All Section
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isAllSelected,
                                    onCheckedChange = { checked ->
                                        selectedIds = if (checked) cartItems.map { it.id }.toSet() else emptySet()
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                                )
                                Text(
                                    "Pilih Semua",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            
                            if (selectedIds.isNotEmpty()) {
                                Text(
                                    "Hapus",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable { 
                                        selectedIds.forEach { onDeleteClick(it) }
                                        selectedIds = emptySet()
                                    }
                                )
                            }
                        }
                    }
                }

                // Cart Items
                items(cartItems) { item ->
                    CartItem(
                        item = item,
                        isSelected = item.id in selectedIds,
                        onSelectionChange = { isChecked ->
                            selectedIds = if (isChecked) selectedIds + item.id else selectedIds - item.id
                        },
                        onDelete = { onDeleteClick(item.id) },
                        onUpdateQty = { qty -> onUpdateQty(item.id, qty) }
                    )
                }

                // Voucher Section
                item {
                    VoucherSection()
                }

                // Recommendations Section
                item {
                    RecommendationSection(onProductClick)
                }
            }
        }
    }
}

@Composable
fun CartItem(
    item: CartEntity,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onUpdateQty: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32)),
                modifier = Modifier.padding(top = 0.dp)
            )

            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.sawid)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.productName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Text(
                    text = "Varian: Standar",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = "Rp ${String.format(Locale.GERMANY, "%,.0f", item.price)}",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = Color(0xFFF5F7F9),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = { if (item.qty > 1) onUpdateQty(item.qty - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Gray)
                            }
                            Text(
                                text = item.qty.toString(),
                                modifier = Modifier.padding(horizontal = 8.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            IconButton(
                                onClick = { onUpdateQty(item.qty + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E7D32))
                            }
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.LightGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VoucherSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F8E9)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ShoppingCart, // Generic icon if Label/Sell is missing
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Pakai Voucher Agrojan",
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("Pilih atau masukkan kode voucher", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Pakai", fontSize = 14.sp)
                }
            }

            Surface(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.checkbox_on_background),
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Voucher: AGROGROW - Hemat 10%",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                    Text(
                        "Batal",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { }
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendationSection(onProductClick: (Long) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            "Lengkapi Kebutuhanmu",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dummy data
            val recommendations = listOf(
                Pair("Alat Cek pH Tanah", 85000.0),
                Pair("Sarung Tangan Karet", 25000.0)
            )
            items(recommendations) { item ->
                Surface(
                    modifier = Modifier
                        .width(160.dp)
                        .clickable { onProductClick(0L) }, // Dummy ID
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column {
                        AsyncImage(
                            model = R.drawable.sawid, // Fallback dummy
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                item.first,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                "Rp ${String.format(Locale.GERMANY, "%,.0f", item.second)}",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartBottomBar(
    totalPrice: Double,
    itemCount: Int,
    onCheckoutClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Total Pembayaran", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        "Rp ${String.format(Locale.GERMANY, "%,.0f", totalPrice)}",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Item Terpilih", color = Color.Gray, fontSize = 12.sp)
                    Text("$itemCount Produk", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Button(
                onClick = onCheckoutClick,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(12.dp),
                enabled = itemCount > 0
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Beli Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("→", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}
