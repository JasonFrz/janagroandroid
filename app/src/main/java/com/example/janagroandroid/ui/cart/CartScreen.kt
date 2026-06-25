package com.example.janagroandroid.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.janagroandroid.R
import com.example.janagroandroid.data.local.entity.CartEntity
import com.example.janagroandroid.ui.theme.JanAgroTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartEntity>,
    onBackClick: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    onDeleteAllClick: () -> Unit,
    onUpdateQty: (Long, Int) -> Unit,
    onCheckoutClick: (Double, LongArray) -> Unit,
    onProductClick: (Long) -> Unit,
    onMerchantClick: (Long) -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    var showClearDialog by remember { mutableStateOf(false) }

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

    val primaryGreen = Color(0xFF2E7D32)
    val darkGreen = Color(0xFF1B5E20)
    val lightGreen = Color(0xFFE8F5E9)

    JanAgroTheme {
        Scaffold(
            topBar = {
                Surface(
                    shadowElevation = 1.dp,
                    color = Color.White,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Keranjang Saya (${cartItems.size})",
                                fontWeight = FontWeight.Bold,
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
                            Text(
                                "Hapus Semua",
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable { if (cartItems.isNotEmpty()) showClearDialog = true },
                                color = if (cartItems.isNotEmpty()) primaryGreen else Color.Gray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Chat",
                                tint = primaryGreen,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                    )
                }
            },
            bottomBar = {
                CartBottomBar(
                    totalPrice = totalPayment,
                    itemCount = selectedItems.size,
                    isAllSelected = isAllSelected,
                    primaryColor = primaryGreen,
                    darkColor = darkGreen,
                    lightColor = lightGreen,
                    onSelectAllChange = { checked ->
                        selectedIds = if (checked) cartItems.map { it.id }.toSet() else emptySet()
                    },
                    onCheckoutClick = { onCheckoutClick(totalPayment, selectedIds.toLongArray()) }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8F5F2))
            ) {
                items(cartItems) { item ->
                    CartItem(
                        item = item,
                        isSelected = item.id in selectedIds,
                        primaryColor = primaryGreen,
                        onSelectionChange = { isChecked ->
                            selectedIds = if (isChecked) selectedIds + item.id else selectedIds - item.id
                        },
                        onDelete = { onDeleteClick(item.id) },
                        onUpdateQty = { qty -> onUpdateQty(item.id, qty) },
                        onMerchantClick = { onMerchantClick(item.merchantId) }
                    )
                }

                item {
                    RecommendationSection(primaryColor = primaryGreen, onProductClick = onProductClick)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Hapus Semua?") },
                text = { Text("Apakah Anda yakin ingin menghapus semua item dari keranjang?") },
                confirmButton = {
                    TextButton(onClick = { 
                        showClearDialog = false
                        onDeleteAllClick()
                    }) {
                        Text("Hapus", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun CartItem(
    item: CartEntity,
    isSelected: Boolean,
    primaryColor: Color,
    onSelectionChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onUpdateQty: (Int) -> Unit,
    onMerchantClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChange,
                    colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                )
                
                Surface(
                    color = primaryColor,
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        "Star+",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                
                Text(
                    item.merchantName.ifEmpty { "Toko Tani Makmur" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f).clickable { onMerchantClick() }
                )
                
                Text(
                    "Delete",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 16.dp).clickable { onDelete() }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = Color(0xFFF2F2F2))

            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChange,
                    colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                )

                AsyncImage(
                    model = if (item.imageUrl.isNullOrEmpty()) R.drawable.farmer else item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.farmer),
                    error = painterResource(id = R.drawable.farmer)
                )

                Column(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = item.productName,
                        fontSize = 14.sp,
                        maxLines = 2,
                        lineHeight = 18.sp
                    )
                    
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Standar, 1kg", fontSize = 12.sp, color = Color.Gray)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rp${String.format(Locale.GERMANY, "%,.0f", item.price)}",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { if (item.qty > 1) onUpdateQty(item.qty - 1) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("-", color = if (item.qty > 1) Color.Black else Color.LightGray)
                            }
                            VerticalDivider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.LightGray)
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(item.qty.toString(), fontSize = 12.sp)
                            }
                            VerticalDivider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.LightGray)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { onUpdateQty(item.qty + 1) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+", color = primaryColor)
                            }
                        }
                    }
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color(0xFFF2F2F2))
        }
    }
}

@Composable
fun RecommendationSection(primaryColor: Color, onProductClick: (Long) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 18.dp)) {
        Text(
            "Rekomendasi Untukmu",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val recommendations = listOf(
                Pair("Pupuk NPK Mutiara", 45000.0),
                Pair("Bibit Sawit Unggul", 125000.0)
            )
            items(recommendations) { item ->
                Surface(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { onProductClick(0L) },
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Column {
                        AsyncImage(
                            model = R.drawable.farmer,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.farmer),
                            error = painterResource(id = R.drawable.farmer)
                        )
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                item.first,
                                fontSize = 12.sp,
                                maxLines = 2,
                                lineHeight = 16.sp
                            )
                            Text(
                                "Rp${String.format(Locale.GERMANY, "%,.0f", item.second)}",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
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
    isAllSelected: Boolean,
    primaryColor: Color,
    darkColor: Color,
    lightColor: Color,
    onSelectAllChange: (Boolean) -> Unit,
    onCheckoutClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(bottom = 28.dp),
        color = Color.White
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = onSelectAllChange,
                        colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                    )
                    Text("Semua", fontSize = 14.sp)
                    
                    Column(
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Total ", fontSize = 12.sp)
                            Text(
                                "Rp${String.format(Locale.GERMANY, "%,.0f", totalPrice)}",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            "Hemat Rp12.145",
                            color = primaryColor,
                            fontSize = 10.sp
                        )
                    }
                }

                Button(
                    onClick = onCheckoutClick,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(130.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkColor),
                    shape = RoundedCornerShape(0.dp),
                    enabled = itemCount > 0
                ) {
                    Text(
                        "Beli (${itemCount})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun Modifier.scale(scale: Float): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelativeWithLayer(0, 0) {
            scaleX = scale
            scaleY = scale
        }
    }
}
