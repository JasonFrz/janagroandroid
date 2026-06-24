package com.example.janagroandroid.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.janagroandroid.R
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.ui.theme.JanAgroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserEntity?,
    onLogoutClick: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onChatListClick: () -> Unit = {},
    onRequestBecomeSeller: (String, String) -> Unit = { _, _ -> },
    onManageProductsClick: () -> Unit = {}
) {
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showBecomeSellerDialog by remember { mutableStateOf(false) }
    
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            onImageSelected(it)
        }
    }

    JanAgroTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Profile", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable(enabled = user != null) { launcher.launch("image/*") }
                ) {
                    val imageSource: Any = profileImageUri ?: user?.profilePicture ?: R.drawable.ic_user_placeholder
                    
                    AsyncImage(
                        model = imageSource,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_user_placeholder),
                        error = painterResource(id = R.drawable.ic_user_placeholder)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user?.name ?: "Guest User",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user?.email ?: "guest@example.com",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Menu Items (Hanya muncul jika sudah login)
                if (user != null) {
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        onClick = { showEditDialog = true }
                    )

                    ProfileMenuItem(
                        icon = Icons.Default.Email,
                        title = "Pesan",
                        onClick = onChatListClick
                    )

                    if (user.role.equals("Seller", ignoreCase = true)) {
                        ProfileMenuItem(
                            icon = Icons.Default.Store,
                            title = "Manage Produk",
                            onClick = onManageProductsClick
                        )
                    }
                    
                    InfoRow(label = "Role", value = user.role)
                    user.phone?.let {
                        if (it.isNotEmpty()) {
                            InfoRow(label = "Phone", value = it)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Bagian Request Become Seller
                if (user != null) {
                    val isSeller = user.role.equals("Seller", ignoreCase = true)
                    if (!isSeller) {
                        if (user.isMerchant) {
                            // User role Customer tetapi sudah request (pending)
                            Text(
                                text = "Anda sudah mengirimkan permintaan menjadi penjual",
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            // Tombol daftar jika belum request
                            Button(
                                onClick = { showBecomeSellerDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Store, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Daftar Jadi Penjual",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    // Jika isSeller == true, maka seluruh blok ini tidak akan muncul (tombol hilang)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tombol Login / Logout
                val isGuest = user == null
                Button(
                    onClick = {
                        if (isGuest) onLogoutClick() else showLogoutDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isGuest) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), // Hijau muda jika guest
                        contentColor = if (isGuest) Color(0xFF2E7D32) else Color.Red // Teks hijau jika guest
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = null,
                    border = null
                ) {
                    Icon(
                        imageVector = if (isGuest) Icons.Default.Login else Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = if (isGuest) Color(0xFF2E7D32) else Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGuest) "Login" else "Logout",
                        color = if (isGuest) Color(0xFF2E7D32) else Color.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Konfirmasi Logout") },
                text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutDialog = false
                            onLogoutClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Ya, Keluar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        if (showEditDialog && user != null) {
            EditProfileDialog(
                currentName = user.name,
                currentPhone = user.phone ?: "",
                onDismiss = { showEditDialog = false },
                onConfirm = { newName, newPhone ->
                    onUpdateProfile(newName, newPhone)
                    showEditDialog = false
                }
            )
        }

        if (showBecomeSellerDialog) {
            BecomeSellerDialog(
                onDismiss = { showBecomeSellerDialog = false },
                onConfirm = { storeName, description ->
                    onRequestBecomeSeller(storeName, description)
                    showBecomeSellerDialog = false
                }
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentPhone: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, phone) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BecomeSellerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var storeName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daftar Jadi Penjual", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Masukkan informasi toko Anda untuk mengajukan pendaftaran menjadi penjual.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Nama Toko") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Toko") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(storeName, description) },
                enabled = storeName.isNotBlank()
            ) {
                Text("Kirim Permintaan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
