package com.example.janagroandroid.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.janagroandroid.data.local.entity.ProductEntity
import com.example.janagroandroid.data.remote.dto.CategoryDto
import com.example.janagroandroid.ui.home.AllProductItem
import com.example.janagroandroid.ui.home.CategoryChip
import com.example.janagroandroid.ui.theme.JanAgroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    initialQuery: String,
    initialCategory: String? = null,
    products: List<ProductEntity>,
    categories: List<CategoryDto>,
    onBackClick: () -> Unit,
    onProductClick: (ProductEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedCategory by remember { mutableStateOf<String?>(initialCategory) }

    val filteredProducts = remember(searchQuery, selectedCategory, products) {
        products.filter { product ->
            val matchesQuery = if (searchQuery.isEmpty()) true else {
                product.name.contains(searchQuery, ignoreCase = true) || 
                product.description.contains(searchQuery, ignoreCase = true)
            }
            val matchesCategory = if (selectedCategory == null) true else {
                product.category.equals(selectedCategory, ignoreCase = true)
            }
            matchesQuery && matchesCategory
        }
    }

    JanAgroTheme {
        Scaffold(
            topBar = {
                Surface(
                    shadowElevation = 0.dp,
                    color = Color.White,
                    modifier = Modifier.statusBarsPadding()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, 
                                    contentDescription = "Back",
                                    tint = Color.Black
                                )
                            }
                            
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                placeholder = { Text("Cari benih, pupuk, atau alat...", fontSize = 14.sp) },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF0F0F0),
                                    unfocusedContainerColor = Color(0xFFF0F0F0),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = { /* Search logic */ }
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        // Category Filter Row
                        if (categories.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                item {
                                    Box(modifier = Modifier.clickable { selectedCategory = null }) {
                                        CategoryChip(
                                            text = "Semua", 
                                            isSelected = selectedCategory == null
                                        )
                                    }
                                }
                                items(categories) { category ->
                                    val categoryName = category.name ?: ""
                                    Box(modifier = Modifier.clickable { 
                                        selectedCategory = if (selectedCategory == categoryName) null else categoryName
                                    }) {
                                        CategoryChip(
                                            text = categoryName,
                                            isSelected = selectedCategory == categoryName
                                        )
                                    }
                                }
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
                    .background(Color(0xFFF5F7F9))
            ) {
                if (filteredProducts.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Search, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Produk tidak ditemukan", color = Color.Gray)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredProducts) { product ->
                            AllProductItem(product = product, onClick = { onProductClick(product) })
                        }
                    }
                }
            }
        }
    }
}
