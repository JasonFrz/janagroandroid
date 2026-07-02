package com.example.janagroandroid.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.remote.dto.CategoryDto
import com.example.janagroandroid.ui.home.AllProductItem
import com.example.janagroandroid.ui.home.CategoryChip
import com.example.janagroandroid.ui.theme.JanAgroTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import com.example.janagroandroid.ui.theme.shimmerModifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    initialQuery: String,
    initialCategory: String? = null,
    products: List<ProductEntity>,
    categories: List<CategoryDto>,
    isLoading: Boolean = false,
    onBackClick: () -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCartClick: (ProductEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedCategory by remember { mutableStateOf<String?>(if (initialCategory == "All") null else initialCategory) }
    
    // Filter states
    var minPrice by remember { mutableStateOf<Double?>(null) }
    var maxPrice by remember { mutableStateOf<Double?>(null) }
    var minRating by remember { mutableStateOf<Int?>(null) }
    
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val filteredProducts = products.filter { product ->
        val matchesQuery = product.name.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || product.category.equals(selectedCategory, ignoreCase = true)
        val matchesMinPrice = minPrice == null || product.price >= minPrice!!
        val matchesMaxPrice = maxPrice == null || product.price <= maxPrice!!
        val matchesRating = minRating == null // Rating logic here if available
        
        matchesQuery && matchesCategory && matchesMinPrice && matchesMaxPrice && matchesRating
    }

    JanAgroTheme {
        Scaffold(
            topBar = {
                Surface(shadowElevation = 2.dp) {
                    Column(modifier = Modifier.background(Color.White)) {
                        CenterAlignedTopAppBar(
                            title = {
                                Text("Explore Products", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            },
                            navigationIcon = {
                                IconButton(onClick = onBackClick) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF2E7D32))
                                }
                            },
                            actions = {
                                IconButton(onClick = { showFilterSheet = true }) {
                                    Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color(0xFF2E7D32))
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                        )
                        
                        // Search Input
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Search products...", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        // Category Tabs
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                CategoryChip(
                                    text = "All",
                                    isSelected = selectedCategory == null,
                                    onClick = { selectedCategory = null }
                                )
                            }
                            items(categories) { category ->
                                val name = category.name ?: ""
                                CategoryChip(
                                    text = name,
                                    isSelected = selectedCategory == name,
                                    onClick = { selectedCategory = name }
                                )
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F7F9))
            ) {
                if (isLoading && products.isEmpty()) {
                    ExploreSkeleton()
                } else if (filteredProducts.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No products found", color = Color.Gray, fontWeight = FontWeight.Medium)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 150.dp // Significant extra padding for the external BottomAppBar
                        ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            AllProductItem(
                                product = product, 
                                onClick = { onProductClick(product) },
                                onAddToCartClick = { onAddToCartClick(product) }
                            )
                        }
                    }
                }
            }
        }

        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                FilterContent(
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    minRating = minRating,
                    onApply = { minP, maxP, minR ->
                        minPrice = minP
                        maxPrice = maxP
                        minRating = minR
                        showFilterSheet = false
                    },
                    onReset = {
                        minPrice = null
                        maxPrice = null
                        minRating = null
                        showFilterSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun FilterContent(
    minPrice: Double?,
    maxPrice: Double?,
    minRating: Int?,
    onApply: (Double?, Double?, Int?) -> Unit,
    onReset: () -> Unit
) {
    var tempMinPrice by remember { mutableStateOf(minPrice?.toString() ?: "") }
    var tempMaxPrice by remember { mutableStateOf(maxPrice?.toString() ?: "") }
    var tempMinRating by remember { mutableStateOf(minRating) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text("Filter Products", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Price Range", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = tempMinPrice,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) tempMinPrice = it },
                modifier = Modifier.weight(1f),
                label = { Text("Min Price") },
                prefix = { Text("Rp ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            OutlinedTextField(
                value = tempMaxPrice,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) tempMaxPrice = it },
                modifier = Modifier.weight(1f),
                label = { Text("Max Price") },
                prefix = { Text("Rp ") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Minimum Rating", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..5).forEach { rating ->
                val isSelected = tempMinRating == rating
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { tempMinRating = if (isSelected) null else rating },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0xFF2E7D32) else Color(0xFFF0F0F0)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = rating.toString(),
                                color = if (isSelected) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
            ) {
                Text("Reset")
            }
            Button(
                onClick = { 
                    onApply(
                        tempMinPrice.toDoubleOrNull(),
                        tempMaxPrice.toDoubleOrNull(),
                        tempMinRating
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Apply Filters")
            }
        }
    }
}

@Composable
fun ExploreSkeleton() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 150.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerModifier()
            )
        }
    }
}
