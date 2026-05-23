package com.example.janagroandroid.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.ui.theme.JanAgroTheme

@Composable
fun HomeScreen(
    user: UserEntity?,
    products: List<ProductEntity>,
    onProfileClick: () -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onSearchClick: () -> Unit,
    onLogoutClick: () -> Unit = {}
) {
    JanAgroTheme {
        Scaffold(
            topBar = {
                // Membungkus Header dalam Surface agar area sentuh terjamin
                Surface(
                    shadowElevation = 2.dp,
                    color = Color.White,
                    modifier = Modifier.statusBarsPadding() // Jarak aman dari Notch/Status Bar
                ) {
                    HomeHeader(
                        user = user, 
                        onProfileClick = onProfileClick, 
                        onSearchClick = onSearchClick,
                        onLogoutClick = onLogoutClick
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF5F7F9))
            ) {
                CategorySection()

                Spacer(modifier = Modifier.height(24.dp))

                ProductSection(
                    title = "Recently Listed",
                    products = products,
                    onProductClick = onProductClick
                )

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun HomeHeader(
    user: UserEntity?,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp), // Dikurangi lagi agar tidak terlalu turun
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.farmer),
            contentDescription = "Profile",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable { onProfileClick() },
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
                .clickable { onProfileClick() }
        ) {
            Text(
                text = user?.name ?: "Guest User",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = if (user != null) "Suroboyo mas, ID" else "Log in for more features",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }

        if (user != null) {
            // Pastikan IconButton memiliki ukuran yang cukup untuk disentuh
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.Red
                )
            }
        } else {
            IconButton(onClick = { /* Menu Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Menu")
            }
        }
    }
}

@Composable
fun CategorySection() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            text = "Shop By Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryChip("Fertilizer", isSelected = true)
            CategoryChip("Seeds", isSelected = false)
            CategoryChip("Tools", isSelected = false)
        }
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean) {
    Surface(
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProductSection(
    title: String,
    products: List<ProductEntity>,
    onProductClick: (ProductEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "View all",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductItem(product = product, onClick = { onProductClick(product) })
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.farmer)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Rp ${product.price}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
