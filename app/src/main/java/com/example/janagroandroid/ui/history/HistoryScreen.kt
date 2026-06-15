package com.example.janagroandroid.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.janagroandroid.data.remote.dto.CategoryDto
import com.example.janagroandroid.data.remote.dto.OrderDto
import com.example.janagroandroid.ui.theme.JanAgroTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    orders: List<OrderDto>,
    categories: List<CategoryDto>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onBuyAgainClick: (Long) -> Unit,
    onDetailClick: (Long) -> Unit
) {
    val primaryGreen = Color(0xFF2E7D32)
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val tabs = listOf("Semua", "Belum Bayar", "Dikemas", "Dikirim", "Selesai", "Batal")

    val filteredOrders = remember(selectedTabIndex, orders, searchQuery) {
        val statusFiltered = when (selectedTabIndex) {
            1 -> orders.filter { it.status.equals("Pending_Payment", ignoreCase = true) }
            2 -> orders.filter { it.status.equals("Paid", ignoreCase = true) || it.status.equals("Packed", ignoreCase = true) }
            3 -> orders.filter { it.status.equals("Shipped", ignoreCase = true) }
            4 -> orders.filter { it.status.equals("Completed", ignoreCase = true) }
            5 -> orders.filter { it.status.equals("Cancelled", ignoreCase = true) }
            else -> orders
        }

        if (searchQuery.isEmpty()) {
            statusFiltered
        } else {
            statusFiltered.filter { order ->
                order.merchant?.storeName?.contains(searchQuery, ignoreCase = true) == true ||
                order.items?.any { it.product?.name?.contains(searchQuery, ignoreCase = true) == true } == true
            }
        }
    }

    JanAgroTheme {
        Scaffold(
            topBar = {
                Surface(
                    shadowElevation = 1.dp,
                    color = Color.White,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    Column {
                        if (isSearchActive) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp)
                                    .padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { 
                                    isSearchActive = false
                                    searchQuery = ""
                                }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Close Search",
                                        tint = primaryGreen
                                    )
                                }
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Cari pesanan atau toko...") },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                            }
                        } else {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        "Pesanan Saya",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 18.sp
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = onBackClick) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = primaryGreen
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { isSearchActive = true }) {
                                        Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryGreen)
                                    }
                                    IconButton(onClick = { }) {
                                        Icon(Icons.Default.Email, contentDescription = "Chat", tint = primaryGreen)
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                            )
                        }
                        
                        ScrollableTabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            contentColor = primaryGreen,
                            edgePadding = 16.dp,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = primaryGreen
                                )
                            },
                            divider = {}
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = {
                                        Text(
                                            text = title,
                                            fontSize = 14.sp,
                                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedTabIndex == index) primaryGreen else Color.Gray
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF2F2F2))
            ) {
                if (isLoading && orders.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryGreen
                    )
                } else if (filteredOrders.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sawid),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Belum ada pesanan",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(filteredOrders) { order ->
                            OrderItem(
                                order = order,
                                categories = categories,
                                primaryColor = primaryGreen,
                                onBuyAgain = { onBuyAgainClick(order.id) },
                                onClick = { onDetailClick(order.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(
    order: OrderDto,
    categories: List<CategoryDto>,
    primaryColor: Color,
    onBuyAgain: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Store Name + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = primaryColor,
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(
                            "Star+",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.merchant?.storeName ?: "Agrojan Store",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = when(order.status) {
                        "Pending_Payment" -> "Belum Bayar"
                        "Paid", "Packed" -> "Dikemas"
                        "Shipped" -> "Dikirim"
                        "Completed" -> "Selesai"
                        "Cancelled" -> "Batal"
                        else -> order.status
                    },
                    fontSize = 13.sp,
                    color = if (order.status == "Cancelled") Color(0xFFD32F2F) else primaryColor
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF2F2F2))

            // Product Info
            val firstItem = order.items?.firstOrNull()
            val categoryName = categories.find { it.id == firstItem?.product?.categoryId }?.name ?: "Umum"

            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = if (firstItem?.product?.firstImageUrl.isNullOrEmpty()) R.drawable.farmer else firstItem?.product?.firstImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF5F5F5)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.farmer),
                    error = painterResource(id = R.drawable.farmer)
                )

                Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(
                        text = firstItem?.product?.name ?: "Pesanan #${order.id}",
                        fontSize = 14.sp,
                        maxLines = 2,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "Kategori: $categoryName",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "x${firstItem?.quantity ?: 1}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        
                        Text(
                            text = "Rp${String.format(Locale.GERMANY, "%,.0f", firstItem?.priceAtPurchaseDouble ?: 0.0)}",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Total Summary
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalItems = order.items?.sumOf { it.quantity } ?: 0
                Text(
                    text = "Total $totalItems produk: ",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Rp${String.format(Locale.GERMANY, "%,.0f", order.totalPriceDouble)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF2F2F2))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (order.status) {
                    "Cancelled" -> {
                        ActionButton(text = "Rincian Pembatalan", onClick = {})
                        Spacer(modifier = Modifier.width(8.dp))
                        ActionButton(text = "Beli Lagi", onClick = onBuyAgain, isPrimary = true, primaryColor = primaryColor)
                    }
                    "Completed" -> {
                        if (order.isReviewed) {
                            ActionButton(text = "Lihat Penilaian", onClick = {})
                        } else {
                            ActionButton(text = "Nilai", onClick = {}, isPrimary = true, primaryColor = primaryColor)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        ActionButton(text = "Beli Lagi", onClick = onBuyAgain, isPrimary = !order.isReviewed, primaryColor = primaryColor)
                    }
                    else -> {
                        ActionButton(text = "Pembayaran", onClick = onBuyAgain, isPrimary = true, primaryColor = primaryColor)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean = false,
    primaryColor: Color = Color(0xFF2E7D32)
) {
    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(4.dp),
        color = if (isPrimary) primaryColor else Color.White,
        border = if (isPrimary) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isPrimary) Color.White else Color.Black
            )
        }
    }
}
