package com.example.janagroandroid.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.janagroandroid.data.remote.dto.VoucherDto
import com.example.janagroandroid.data.remote.dto.VoucherRequest
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory
import com.example.janagroandroid.ui.theme.JanAgroTheme

class AdminVouchersFragment : Fragment() {

    private val viewModel: AdminVouchersViewModel by viewModels{
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel.loadVouchers()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AdminVouchersScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVouchersScreen(viewModel: AdminVouchersViewModel) {
    val vouchers by viewModel.vouchers.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    // Kontrol dialog:
    //   showDialog = true  -> dialog tampil
    //   editing == null    -> mode TAMBAH
    //   editing != null    -> mode EDIT (isi form diisi dari voucher itu)
    var showDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<VoucherDto?>(null) }

    JanAgroTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Voucher Management") },
                    actions = {
                        IconButton(onClick = {
                            editing = null
                            showDialog = true
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah voucher")
                        }
                    }
                )
            }
        ){ padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when {
                    isLoading && vouchers.isEmpty() -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    vouchers.isEmpty() -> {
                        Text("Belum ada voucher.", Modifier.align(Alignment.Center), color = Color.Gray)
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 96.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(vouchers) { voucher ->
                                VoucherCard(
                                    voucher = voucher,
                                    onEdit = {
                                        editing = voucher    // mode edit
                                        showDialog = true
                                    },
                                    onDelete = { viewModel.deleteVoucher(voucher.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            VoucherFormDialog(
                initial = editing,
                onDismiss = { showDialog = false },
                onSubmit = { request ->
                    if (editing == null) {
                        viewModel.createVoucher(request)
                    } else {
                        viewModel.updateVoucher(editing!!.id, request)
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun VoucherCard(
    voucher: VoucherDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(voucher.code, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                val potongan = if (voucher.discountType == "Percentage")
                    "${voucher.discountValue}%" else "Rp ${voucher.discountValue}"
                Text("Diskon: $potongan", fontSize = 13.sp, color = Color.Gray)
                Text("Kuota: ${voucher.quota}", fontSize = 13.sp, color = Color.Gray)
                if (voucher.startDate != null && voucher.endDate != null) {
                    Text(
                        "Periode: ${voucher.startDate.take(10)} s/d ${voucher.endDate.take(10)}",
                        fontSize = 12.sp, color = Color.Gray
                    )
                }
                Text(
                    text = if (voucher.isActive) "Aktif" else "Nonaktif",
                    fontSize = 12.sp,
                    color = if (voucher.isActive) Color(0xFF2E7D32) else Color.Red
                )
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Hapus") }
        }
    }
}

@Composable
private fun VoucherFormDialog(
    initial: VoucherDto?,
    onDismiss: () -> Unit,
    onSubmit: (VoucherRequest) -> Unit
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var discountType by remember { mutableStateOf(initial?.discountType ?: "Fixed_Amount") }
    var discountValue by remember { mutableStateOf(initial?.discountValue ?: "") }
    var minPurchase by remember { mutableStateOf(initial?.minPurchase ?: "0") }
    var maxDiscount by remember { mutableStateOf(initial?.maxDiscount ?: "") }
    var quota by remember { mutableStateOf(initial?.quota?.toString() ?: "0") }
    var maxUsesPerUser by remember { mutableStateOf(initial?.maxUsesPerUser?.toString() ?: "1") }
    var startDate by remember { mutableStateOf(initial?.startDate?.take(10) ?: "") }
    var endDate by remember { mutableStateOf(initial?.endDate?.take(10) ?: "") }
    var isActive by remember { mutableStateOf(initial?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Tambah Voucher" else "Edit Voucher") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(code, { code = it }, label = { Text("Kode") }, singleLine = true)
                OutlinedTextField(description, { description = it }, label = { Text("Deskripsi") })

                Text("Tipe diskon", fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = discountType == "Fixed_Amount",
                        onClick = { discountType = "Fixed_Amount" },
                        label = { Text("Nominal (Rp)") }
                    )
                    FilterChip(
                        selected = discountType == "Percentage",
                        onClick = { discountType = "Percentage" },
                        label = { Text("Persen (%)") }
                    )
                }

                OutlinedTextField(discountValue, { discountValue = it }, label = { Text("Nilai diskon") }, singleLine = true)
                OutlinedTextField(minPurchase, { minPurchase = it }, label = { Text("Min. pembelian") }, singleLine = true)
                OutlinedTextField(maxDiscount, { maxDiscount = it }, label = { Text("Maks. diskon (opsional)") }, singleLine = true)
                OutlinedTextField(quota, { quota = it }, label = { Text("Kuota") }, singleLine = true)
                OutlinedTextField(maxUsesPerUser, { maxUsesPerUser = it }, label = { Text("Maks. pakai / user") }, singleLine = true)
                OutlinedTextField(startDate, { startDate = it }, label = { Text("Mulai (YYYY-MM-DD)") }, singleLine = true)
                OutlinedTextField(endDate, { endDate = it }, label = { Text("Berakhir (YYYY-MM-DD)") }, singleLine = true)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Aktif")
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = code.isNotBlank() && discountValue.isNotBlank()
                        && startDate.isNotBlank() && endDate.isNotBlank(),
                onClick = {
                    onSubmit(
                        VoucherRequest(
                            code = code.trim(),
                            description = description.ifBlank { null },
                            discountType = discountType,
                            discountValue = discountValue,
                            minPurchase = minPurchase.ifBlank { "0" },
                            maxDiscount = maxDiscount.ifBlank { null },
                            quota = quota.toIntOrNull() ?: 0,
                            maxUsesPerUser = maxUsesPerUser.toIntOrNull() ?: 1,
                            startDate = startDate,
                            endDate = endDate,
                            isActive = isActive
                        )
                    )
                }
            ) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}