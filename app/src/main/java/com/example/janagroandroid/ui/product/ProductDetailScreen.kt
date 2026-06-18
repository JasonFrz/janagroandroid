package com.example.janagroandroid.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import coil.compose.AsyncImage
import com.example.janagroandroid.R
import com.example.janagroandroid.data.remote.dto.ReviewDto
import com.example.janagroandroid.ui.theme.JanAgroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    id: Long,
    name: String,
    price: Double,
    imageUrl: String,
    description: String,
    merchantName: String = "",
    merchantAddress: String = "",
    category: String = "",
    reviews: List<ReviewDto> = emptyList(),
    onBackClick: () -> Unit,
    onAddToCartClick: (Int) -> Unit,
    onChatClick: () -> Unit = {}
) {
    val avgRating = if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
    val totalSold = (reviews.size * 3) + 7 // Mock sold count based on reviews

    JanAgroTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "AGROJAN",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    },
                    actions = {
//                        IconButton(onClick = { /* Share */ }) {
//                            Icon(
//                                imageVector = Icons.Outlined.Share,
//                                contentDescription = "Share",
//                                tint = Color(0xFF2E7D32)
//                            )
//                        }
                        IconButton(onClick = { /* Cart */ }) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            bottomBar = {
                BottomActionBar(
                    onAddToCartClick = { onAddToCartClick(1) },
                    onChatClick = onChatClick
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF8F8F8))
            ) {
                // Product Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.White)
                ) {
                    AsyncImage(
                        model = if (imageUrl.isNullOrEmpty()) R.drawable.farmer else imageUrl,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(id = R.drawable.farmer),
                        error = painterResource(id = R.drawable.farmer)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Tag and Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFFFF9C4),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = category.ifBlank { "Produk" },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBC02D)
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (avgRating > 0) Color(0xFFFFB300) else Color(0xFF757575),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = " ${String.format(Locale.US, "%.1f", avgRating)} ($totalSold terjual)",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Rp ${String.format(Locale.US, "%,.0f", price)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = merchantAddress.ifBlank { "Surabaya, Jawa Timur" },
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Merchant Section
                MerchantSection(merchantName.ifBlank { "Agrojan Store" })

                Spacer(modifier = Modifier.height(8.dp))

                // Description Section
                DescriptionSection(description)

                Spacer(modifier = Modifier.height(8.dp))

                // Reviews Section
                ReviewSection(reviews)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MerchantSection(merchantName: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F7F9))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.farmer,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.farmer),
                error = painterResource(id = R.drawable.farmer)
            )
            
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = merchantName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Online baru saja",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            OutlinedButton(
                onClick = { /* Go to store */ },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32))
            ) {
                Text("Kunjungi\nToko", fontSize = 12.sp, lineHeight = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
fun DescriptionSection(description: String) {
    var isExpanded by remember { mutableStateOf(false) }
    val displayDescription = description.ifEmpty { "Tidak ada deskripsi produk!" }
    val showReadMore = displayDescription.length > 100

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Deskripsi Produk",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isExpanded || !showReadMore) displayDescription else "${displayDescription.take(100)}...",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
        
        if (showReadMore) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Sembunyikan" else "Baca Selengkapnya",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
fun ReviewSection(reviews: List<ReviewDto>) {
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filterOptions = listOf("Semua", "5 ★", "4 ★", "3 ★")

    val filteredReviews = remember(selectedFilter, reviews) {
        if (selectedFilter == "Semua") {
            reviews
        } else {
            val rating = selectedFilter.split(" ").firstOrNull()?.toIntOrNull()
            if (rating != null) {
                reviews.filter { it.rating == rating }
            } else {
                reviews
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ulasan Pembeli",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Lihat Semua",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { option ->
                FilterChip(
                    selected = selectedFilter == option,
                    label = option,
                    onClick = { selectedFilter = option }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (filteredReviews.isEmpty()) {
            Text(
                text = if (selectedFilter == "Semua") "Belum ada ulasan untuk produk ini." else "Tidak ada ulasan dengan rating ini.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            val itemsToShow = filteredReviews.take(3)
            itemsToShow.forEachIndexed { index, review ->
                ReviewItem(
                    name = review.user?.name ?: "User #${review.userId}",
                    date = review.createdAt.split("T").firstOrNull() ?: "",
                    rating = review.rating,
                    comment = review.comment ?: "Tidak ada komentar"
                )
                
                if (index < itemsToShow.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                }
            }
        }
    }
}

@Composable
fun FilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    Surface(
        color = if (selected) Color(0xFF2E7D32) else Color(0xFFF5F7F9),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) Color.White else Color.Gray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ReviewItem(name: String, date: String, rating: Int, comment: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = name.first().toString(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFD600) else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            Text(text = date, fontSize = 12.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = comment,
            fontSize = 14.sp,
            color = Color.Black,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun BottomActionBar(onAddToCartClick: () -> Unit, onChatClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onChatClick() }.padding(end = 16.dp)
            ) {
                Icon(Icons.Outlined.Email, contentDescription = "Chat", tint = Color.Gray)
                Text("Chat", fontSize = 10.sp, color = Color.Gray)
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onAddToCartClick() }.padding(end = 16.dp)
            ) {
                Icon(Icons.Outlined.ShoppingCart, contentDescription = "Keranjang", tint = Color.Gray)
                Text("Keranjang", fontSize = 10.sp, color = Color.Gray)
            }
            
            Button(
                onClick = { /* Buy Now */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Text("Beli Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}