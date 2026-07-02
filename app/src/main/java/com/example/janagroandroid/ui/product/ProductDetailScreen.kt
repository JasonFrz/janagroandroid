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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
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
import androidx.compose.ui.text.AnnotatedString
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
import com.example.janagroandroid.ui.theme.shimmerModifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    id: Long,
    name: String,
    price: Double,
    imageUrl: String,
    description: String,
    isLoading: Boolean = false,
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
            if (isLoading) {
                ProductDetailSkeleton(modifier = Modifier.padding(padding))
            } else {
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
                                    Text("Beli $wholesaleMinQty atau lebih", color = Color.Gray, fontSize = 12.sp)
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
    }

        if (showAiTipsSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAiTipsSheet = false },
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() },
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    // Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Surface(
                            color = Color(0xFFF3E5F5),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✨", fontSize = 20.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tips Cerdas Gemini AI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF6A1B9A)
                            )
                            Text(
                                text = "Panduan & Cara Penggunaan",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        IconButton(onClick = { showAiTipsSheet = false }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isAiLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF6A1B9A),
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Sedang meramu tips untukmu...",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else if (aiTips != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false)
                                .verticalScroll(rememberScrollState())
                        ) {
                            MarkdownText(text = aiTips)
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color.Red.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Gagal memuat tips AI. Silakan coba lagi nanti.",
                                color = Color.Red,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
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
fun ProductDetailSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        // Image Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.White)
                .shimmerModifier()
        )
        
        // Info Section Skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.width(80.dp).height(20.dp).clip(RoundedCornerShape(16.dp)).shimmerModifier())
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth(0.7f).height(28.dp).clip(RoundedCornerShape(4.dp)).shimmerModifier())
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(120.dp).height(32.dp).clip(RoundedCornerShape(4.dp)).shimmerModifier())
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.width(200.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).shimmerModifier())
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Merchant Section Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerModifier()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Description Skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.width(150.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerModifier())
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(4.dp)).shimmerModifier())
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
                CustomFilterChip(
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
fun CustomFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
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
    // Split text by numbered headers (e.g., "1. ", "2. ")
    val sectionRegex = Regex("(?m)^(?=\\d+\\.)")
    val sections = text.split(sectionRegex).filter { it.isNotBlank() }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        sections.forEach { section ->
            MarkdownSectionItem(section)
        }
    }
}

@Composable
fun MarkdownSectionItem(sectionContent: String) {
    val lines = sectionContent.trim().lines()
    val headerLine = lines.firstOrNull() ?: ""
    val bodyLines = lines.drop(1)

    Surface(
        color = Color(0xFFFBF9FF), // Very light purple/white
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF3E5F5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Text(
                text = parseMarkdownInline(headerLine),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A1B9A),
                    fontSize = 16.sp
                )
            )
            
            if (bodyLines.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                bodyLines.forEach { line ->
                    val trimmed = line.trim()
                    if (trimmed.isEmpty()) return@forEach
                    
                    if (trimmed.startsWith("*") || trimmed.startsWith("•") || trimmed.startsWith("-")) {
                        Row(modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)) {
                            Text("•", color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = parseMarkdownInline(trimmed.removePrefix("*").removePrefix("•").removePrefix("-").trim()),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF424242),
                                    lineHeight = 22.sp,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    } else {
                        Text(
                            text = parseMarkdownInline(trimmed),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF424242),
                                lineHeight = 22.sp,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

fun parseMarkdownInline(text: String): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        // Match **text** (Bold)
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        val matches = boldRegex.findAll(text)
        
        for (match in matches) {
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            val matchText = match.groupValues[1]

            // Append text before bold
            append(text.substring(currentIndex, startIndex))
            
            // Append bold text
            withStyle(style = SpanStyle(
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF212121)
            )) {
                append(matchText)
            }
            
            currentIndex = endIndex
        }
        
        // Append remaining text
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}
