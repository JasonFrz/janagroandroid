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
    var selectedCategory by remember { mutableStateOf<String?>(if (initialCategory == "All") null else initialCategory) }
    
    // Filter states
    var minPrice by remember { mutableStateOf<Double?>(null) }
    var maxPrice by remember { mutableStateOf<Double?>(null) }
    var minRating by remember { mutableStateOf<Int?>(null) }
    
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val filteredProducts = remember(searchQuery, selectedCategory, minPrice, maxPrice, minRating, products) {
        products.filter { product ->
            val matchesQuery = if (searchQuery.isEmpty()) true else {
                product.name.contains(searchQuery, ignoreCase = true) || 
                product.description.contains(searchQuery, ignoreCase = true)
            }
            val matchesCategory = if (selectedCategory == null || selectedCategory == "All") true else {
                product.category.equals(selectedCategory, ignoreCase = true)
            }
            val matchesMinPrice = minPrice?.let { product.price >= it } ?: true
            val matchesMaxPrice = maxPrice?.let { product.price <= it } ?: true
            
            matchesQuery && matchesCategory && matchesMinPrice && matchesMaxPrice
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
                            
                            IconButton(onClick = { showFilterSheet = true }) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                        }

                        // Category Filter Row
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                CategoryChip(
                                    text = "Semua", 
                                    isSelected = selectedCategory == null || selectedCategory == "All",
                                    onClick = { selectedCategory = null }
                                )
                            }
                            items(categories) { category ->
                                val categoryName = category.name ?: ""
                                CategoryChip(
                                    text = categoryName,
                                    isSelected = selectedCategory == categoryName,
                                    onClick = { 
                                        selectedCategory = if (selectedCategory == categoryName) null else categoryName
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
            
            if (showFilterSheet) {
                FilterBottomSheet(
                    sheetState = sheetState,
                    currentMinPrice = minPrice,
                    currentMaxPrice = maxPrice,
                    currentMinRating = minRating,
                    currentCategory = selectedCategory,
                    categories = categories,
                    onDismiss = { showFilterSheet = false },
                    onApply = { min, max, rating, cat ->
                        minPrice = min
                        maxPrice = max
                        minRating = rating
                        selectedCategory = cat
                        showFilterSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    currentMinPrice: Double?,
    currentMaxPrice: Double?,
    currentMinRating: Int?,
    currentCategory: String?,
    categories: List<CategoryDto>,
    onDismiss: () -> Unit,
    onApply: (Double?, Double?, Int?, String?) -> Unit
) {
    var minPriceInput by remember { mutableStateOf(currentMinPrice?.toInt()?.toString() ?: "") }
    var maxPriceInput by remember { mutableStateOf(currentMaxPrice?.toInt()?.toString() ?: "") }
    var selectedRating by remember { mutableStateOf(currentMinRating) }
    var selectedCategory by remember { mutableStateOf(currentCategory) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Text(
                    text = "Pilih Preferensi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Price Range
                Text("Batas Harga", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minPriceInput,
                        onValueChange = { minPriceInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("MIN", fontSize = 14.sp, color = Color.LightGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                    Text("-", color = Color.LightGray)
                    OutlinedTextField(
                        value = maxPriceInput,
                        onValueChange = { maxPriceInput = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("MAX", fontSize = 14.sp, color = Color.LightGray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriceQuickChip("0-75RB") { minPriceInput = "0"; maxPriceInput = "75000" }
                    PriceQuickChip("75RB-150RB") { minPriceInput = "75000"; maxPriceInput = "150000" }
                    PriceQuickChip("150RB-200RB") { minPriceInput = "150000"; maxPriceInput = "200000" }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rating
                Text("Penilaian", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    RatingChip("5", isSelected = selectedRating == 5) { selectedRating = 5 }
                    RatingChip("≥4", isSelected = selectedRating == 4) { selectedRating = 4 }
                    RatingChip("≥3", isSelected = selectedRating == 3) { selectedRating = 3 }
                    RatingChip("≥2", isSelected = selectedRating == 2) { selectedRating = 2 }
                    RatingChip("≥1", isSelected = selectedRating == 1) { selectedRating = 1 }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Categories
                Text("Berdasarkan Kategori", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    categories.forEach { category ->
                        val name = category.name ?: ""
                        FilterCategoryChip(
                            text = name,
                            isSelected = selectedCategory == name,
                            onClick = { 
                                selectedCategory = if (selectedCategory == name) null else name 
                            }
                        )
                    }
                }
                
                Text(
                    "Lihat Lainnya ∨",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp).clickable { }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        minPriceInput = ""
                        maxPriceInput = ""
                        selectedRating = null
                        selectedCategory = null
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text("Atur Ulang", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onApply(
                            minPriceInput.toDoubleOrNull(),
                            maxPriceInput.toDoubleOrNull(),
                            selectedRating,
                            selectedCategory
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Terapkan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PriceQuickChip(text: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFFF5F7F9),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun RatingChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F7F9),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)) else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = if (isSelected) Color(0xFF2E7D32) else Color.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun FilterCategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F7F9),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32)) else null,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = if (isSelected) Color(0xFF2E7D32) else Color.Black
        )
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeholders = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentRowWidth = 0

        placeholders.forEach { placeable ->
            if (currentRowWidth + placeable.width + mainAxisSpacing.roundToPx() > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                currentRowWidth = 0
            }
            currentRow.add(placeable)
            currentRowWidth += placeable.width + mainAxisSpacing.roundToPx()
        }
        if (currentRow.isNotEmpty()) rows.add(currentRow)

        val height = rows.sumOf { row -> row.maxOf { it.height } } + (rows.size - 1) * crossAxisSpacing.roundToPx()
        val width = constraints.maxWidth

        layout(width, height) {
            var y = 0
            rows.forEach { row ->
                var x = 0
                val rowHeight = row.maxOf { it.height }
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += placeable.width + mainAxisSpacing.roundToPx()
                }
                y += rowHeight + crossAxisSpacing.roundToPx()
            }
        }
    }
}
