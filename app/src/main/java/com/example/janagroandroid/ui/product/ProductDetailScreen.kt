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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
    merchantId: Long = 0L,
    merchantUserId: Long = 0L,
    merchantName: String = "",
    merchantAddress: String = "",
    merchantProfileUrl: String = "",
    category: String = "",
    reviews: List<ReviewDto> = emptyList(),
    aiTips: String? = null,
    isAiLoading: Boolean = false,
    activeNegotiation: com.example.janagroandroid.data.remote.dto.ActiveNegotiationData? = null,
    onAiTipsClick: () -> Unit = {},
    onBackClick: () -> Unit,
    onAddToCartClick: (Int) -> Unit,
    onBuyNowClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onChatClick: (Long, String) -> Unit = { _, _ -> },
    onMerchantClick: (Long) -> Unit = {},
    wholesaleMinQty: Int? = null,
    wholesalePrice: Double? = null,
    onNegoSubmit: (String) -> Unit = {}
) {
    val avgRating = if (reviews.isEmpty()) 0.0 else reviews.map { it.rating }.average()
    val totalSold = (reviews.size * 3) + 7 // Mock sold count based on reviews
    
    var showAiTipsSheet by remember { mutableStateOf(false) }
    var showNegoDialog by remember { mutableStateOf(false) }
    var negoPriceInput by remember { mutableStateOf("") }

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
                        IconButton(onClick = onCartClick) {
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
                    onBuyNowClick = onBuyNowClick,
                    onChatClick = {
                        val finalPartnerId = if (merchantUserId != 0L) merchantUserId else merchantId
                        onChatClick(finalPartnerId, merchantName)
                    },
                    onNegoClick = { showNegoDialog = true }
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
                    }


                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val displayPrice = if (activeNegotiation?.active == true && activeNegotiation.negotiatedPrice != null) {
                        activeNegotiation.negotiatedPrice
                    } else {
                        price
                    }

                    Text(
                        text = "Rp ${String.format(Locale.US, "%,.0f", displayPrice)}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )

                    if (activeNegotiation?.active == true) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Harga Nego Aktif (Sisa: ${activeNegotiation.remainingMinutes ?: 0} mnt)",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

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

                    if (wholesaleMinQty != null && wholesalePrice != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Harga Grosir", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 14.sp)
                                    Text("Beli ${wholesaleMinQty} atau lebih", color = Color.Gray, fontSize = 12.sp)
                                }
                                Text("Rp ${String.format(Locale.US, "%,.0f", wholesalePrice)}/pcs", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Merchant Section
                ProductMerchantSection(
                    merchantName = merchantName.ifBlank { "Agrojan Store" },
                    merchantProfileUrl = merchantProfileUrl,
                    onVisitStoreClick = { onMerchantClick(merchantId) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // AI Tips Button Section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable {
                            onAiTipsClick()
                            showAiTipsSheet = true
                        },
                    color = Color(0xFFF3E5F5), // Light purple background
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✨", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tanya Gemini AI",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6A1B9A),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Dapatkan panduan cerdas cara penggunaan dan tips untuk produk ini.",
                                color = Color(0xFF8E24AA),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description Section
                DescriptionSection(description)

                Spacer(modifier = Modifier.height(8.dp))

                // Reviews Section
                ReviewSection(reviews)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (showAiTipsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAiTipsSheet = false },
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    if (isAiLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF6A1B9A))
                        }
                    } else if (aiTips != null) {
                        MarkdownText(
                            text = aiTips,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    } else {
                        Text(
                            text = "Gagal memuat tips AI. Silakan coba lagi nanti.",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        
        if (showNegoDialog) {
            AlertDialog(
                onDismissRequest = { showNegoDialog = false },
                title = { Text("Ajukan Harga Nego", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Masukkan harga yang ingin Anda ajukan untuk produk ini:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = negoPriceInput,
                            onValueChange = { if (it.all { char -> char.isDigit() }) negoPriceInput = it },
                            label = { Text("Harga Nego (Rp)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (negoPriceInput.isNotBlank()) {
                                showNegoDialog = false
                                onNegoSubmit(negoPriceInput)
                                negoPriceInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Ajukan", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNegoDialog = false }) {
                        Text("Batal", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun ProductMerchantSection(merchantName: String, merchantProfileUrl: String = "", onVisitStoreClick: () -> Unit = {}) {
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
                model = if (merchantProfileUrl.isNotBlank()) merchantProfileUrl.replace("http://", "https://") else R.drawable.farmer,
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
                onClick = onVisitStoreClick,
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
                    name = review.reviewer?.name ?: "User #${review.userId}",
                    date = review.createdAt.split("T").firstOrNull() ?: "",
                    rating = review.rating,
                    comment = review.comment ?: "Tidak ada komentar",
                    imageUrl = review.imageUrl
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
fun ReviewItem(name: String, date: String, rating: Int, comment: String, imageUrl: String? = null) {
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
        if (!imageUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = imageUrl,
                contentDescription = "Review Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun BottomActionBar(onAddToCartClick: () -> Unit, onBuyNowClick: () -> Unit = {}, onChatClick: () -> Unit = {}, onNegoClick: () -> Unit = {}) {
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
                Text("Beli", fontSize = 10.sp, color = Color.Gray)
            }
            
            Button(
                onClick = onNegoClick,
                modifier = Modifier
                    .weight(0.7f)
                    .height(48.dp)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57F17))
            ) {
                Text("Nego", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Button(
                onClick = onBuyNowClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Text("Beli Sekarang", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    // 1. Ganti bullet point markdown (asterisk di awal baris) dengan bullet symbol
    val bulletRegex = "(?m)^\\s*\\*\\s+".toRegex()
    val cleanedText = text.replace(bulletRegex, "• ")
    
    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        // Match **text**
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        val matches = boldRegex.findAll(cleanedText)
        
        for (match in matches) {
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            val matchText = match.groupValues[1]

            // Append text before bold
            append(cleanedText.substring(currentIndex, startIndex))
            
            // Append bold text
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                append(matchText)
            }
            
            currentIndex = endIndex
        }
        
        // Append remaining text
        if (currentIndex < cleanedText.length) {
            append(cleanedText.substring(currentIndex))
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier,
        fontSize = 14.sp,
        color = Color(0xFF424242), // Darker gray for better readability
        lineHeight = 24.sp
    )
}
