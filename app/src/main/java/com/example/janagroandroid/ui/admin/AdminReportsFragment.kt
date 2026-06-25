package com.example.janagroandroid.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.janagroandroid.data.remote.dto.AdminProductDto
import com.example.janagroandroid.data.remote.dto.AdminReviewDto
import com.example.janagroandroid.data.remote.dto.AdminUserDto
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import com.example.janagroandroid.ui.theme.JanAgroTheme
import kotlinx.coroutines.delay

class AdminReportsFragment : Fragment() {

    private val viewModel: AdminReportsViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel.loadAll()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AdminReportsScreen(viewModel)
            }
        }
    }
}

private enum class ModerationTab(val label: String) {
    USERS("Pengguna"),
    PRODUCTS("Produk"),
    REVIEWS("Ulasan")
}

/** A pending destructive action awaiting confirmation in a dialog. */
private data class ConfirmAction(
    val title: String,
    val text: String,
    val confirmLabel: String,
    val onConfirm: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(viewModel: AdminReportsViewModel) {
    val context = LocalContext.current
    val message by viewModel.message.observeAsState()
    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.consumeMessage()
        }
    }

    var selectedTab by remember { mutableStateOf(ModerationTab.USERS) }
    var confirm by remember { mutableStateOf<ConfirmAction?>(null) }

    JanAgroTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Moderasi & Laporan") }) }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                TabRow(selectedTabIndex = selectedTab.ordinal) {
                    ModerationTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = { Text(tab.label) }
                        )
                    }
                }

                when (selectedTab) {
                    ModerationTab.USERS -> UsersTab(viewModel) { confirm = it }
                    ModerationTab.PRODUCTS -> ProductsTab(viewModel) { confirm = it }
                    ModerationTab.REVIEWS -> ReviewsTab(viewModel) { confirm = it }
                }
            }
        }

        confirm?.let { action ->
            AlertDialog(
                onDismissRequest = { confirm = null },
                title = { Text(action.title) },
                text = { Text(action.text) },
                confirmButton = {
                    TextButton(onClick = {
                        action.onConfirm()
                        confirm = null
                    }) { Text(action.confirmLabel, color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { confirm = null }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
private fun SearchField(value: String, onChange: (String) -> Unit, hint: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(hint) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun TabBody(isLoading: Boolean, isEmpty: Boolean, emptyText: String, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading && isEmpty -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            isEmpty -> Text(emptyText, Modifier.align(Alignment.Center), color = Color.Gray)
            else -> content()
        }
    }
}

// ── Users tab ─────────────────────────────────────────────────────────────────
@Composable
private fun UsersTab(viewModel: AdminReportsViewModel, onConfirm: (ConfirmAction) -> Unit) {
    val users by viewModel.users.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    var query by remember { mutableStateOf("") }

    // Cari otomatis dengan debounce; query kosong menampilkan semua pengguna.
    LaunchedEffect(query) {
        delay(350)
        viewModel.loadUsers(query.trim().ifBlank { null })
    }

    Column {
        SearchField(query, { query = it }, "Cari nama / email")
        TabBody(isLoading, users.isEmpty(), "Tidak ada pengguna.") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user -> UserCard(user, onConfirm, viewModel) }
            }
        }
    }
}

@Composable
private fun UserCard(user: AdminUserDto, onConfirm: (ConfirmAction) -> Unit, viewModel: AdminReportsViewModel) {
    val isBanned = user.status.equals("Banned", ignoreCase = true)
    val isAdmin = user.role.equals("Admin", ignoreCase = true)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(user.email, fontSize = 13.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Role: ${user.role}", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isBanned) "DIBAN" else "Aktif",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBanned) Color.Red else Color(0xFF2E7D32)
                )
            }
            if (!isAdmin) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isBanned) {
                        OutlinedButton(onClick = { viewModel.unbanUser(user.id) }) { Text("Buka Ban") }
                    } else {
                        OutlinedButton(onClick = {
                            onConfirm(
                                ConfirmAction(
                                    "Ban Pengguna",
                                    "Ban ${user.name}? Pengguna tidak bisa mengakses aplikasi.",
                                    "Ban"
                                ) { viewModel.banUser(user.id) }
                            )
                        }) { Text("Ban") }
                    }
                    Button(
                        onClick = {
                            onConfirm(
                                ConfirmAction(
                                    "Hapus Pengguna",
                                    "Hapus akun ${user.name}? Tindakan ini permanen.",
                                    "Hapus"
                                ) { viewModel.deleteUser(user.id) }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Hapus") }
                }
            }
        }
    }
}

// ── Products tab ──────────────────────────────────────────────────────────────
@Composable
private fun ProductsTab(viewModel: AdminReportsViewModel, onConfirm: (ConfirmAction) -> Unit) {
    val products by viewModel.products.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    var query by remember { mutableStateOf("") }

    LaunchedEffect(query) {
        delay(350)
        viewModel.loadProducts(query.trim().ifBlank { null })
    }

    Column {
        SearchField(query, { query = it }, "Cari produk")
        TabBody(isLoading, products.isEmpty(), "Tidak ada produk.") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product -> ProductCard(product, onConfirm, viewModel) }
            }
        }
    }
}

@Composable
private fun ProductCard(product: AdminProductDto, onConfirm: (ConfirmAction) -> Unit, viewModel: AdminReportsViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Rp ${product.price ?: "-"}  •  Stok: ${product.stock}", fontSize = 13.sp, color = Color.Gray)
                val seller = product.merchant?.storeName ?: product.merchant?.owner?.name ?: "-"
                Text("Penjual: $seller", fontSize = 12.sp, color = Color.Gray)
            }
            Button(
                onClick = {
                    onConfirm(
                        ConfirmAction(
                            "Hapus Produk",
                            "Hapus produk \"${product.name}\" dari katalog?",
                            "Hapus"
                        ) { viewModel.deleteProduct(product.id) }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) { Text("Hapus") }
        }
    }
}

// ── Reviews tab ───────────────────────────────────────────────────────────────
@Composable
private fun ReviewsTab(viewModel: AdminReportsViewModel, onConfirm: (ConfirmAction) -> Unit) {
    val reviews by viewModel.reviews.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    TabBody(isLoading, reviews.isEmpty(), "Tidak ada ulasan.") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews) { review -> ReviewCard(review, onConfirm, viewModel) }
        }
    }
}

@Composable
private fun ReviewCard(review: AdminReviewDto, onConfirm: (ConfirmAction) -> Unit, viewModel: AdminReportsViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("★ ${review.rating}/5", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFF9A825))
                Spacer(Modifier.width(8.dp))
                if (review.isHidden) {
                    Text("DISEMBUNYIKAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }
            Text(review.product?.name ?: "-", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            if (!review.comment.isNullOrBlank()) {
                Text("\"${review.comment}\"", fontSize = 13.sp, color = Color.DarkGray)
            }
            Text("Oleh: ${review.reviewer?.name ?: "-"}", fontSize = 12.sp, color = Color.Gray)
            Row(modifier = Modifier.padding(top = 8.dp)) {
                if (review.isHidden) {
                    OutlinedButton(onClick = { viewModel.setReviewHidden(review.id, false) }) { Text("Tampilkan") }
                } else {
                    OutlinedButton(onClick = {
                        onConfirm(
                            ConfirmAction(
                                "Sembunyikan Ulasan",
                                "Sembunyikan ulasan ini dari publik?",
                                "Sembunyikan"
                            ) { viewModel.setReviewHidden(review.id, true) }
                        )
                    }) { Text("Sembunyikan") }
                }
            }
        }
    }
}
