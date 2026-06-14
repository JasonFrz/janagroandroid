package com.example.janagroandroid.ui.seller

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.janagroandroid.R
import com.example.janagroandroid.databinding.FragmentAddProductBinding
import com.example.janagroandroid.databinding.ItemImagePreviewBinding
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddProductViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    private var selectedImageUris = mutableListOf<Uri>()

    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            val totalAfterAdd = selectedImageUris.size + uris.size
            if (totalAfterAdd > 5) {
                Toast.makeText(requireContext(), "Maksimal 5 gambar diperbolehkan", Toast.LENGTH_SHORT).show()
                val availableSlot = 5 - selectedImageUris.size
                if (availableSlot > 0) {
                    selectedImageUris.addAll(uris.take(availableSlot))
                }
            } else {
                selectedImageUris.addAll(uris)
            }
            updateImagePreviews()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAddProductBinding.bind(view)

        // Setup Toolbar
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        setupCategoryDropdown()
        setupStockCounter()

        binding.btnSelectImages.setOnClickListener {
            if (selectedImageUris.size >= 5) {
                Toast.makeText(requireContext(), "Sudah mencapai batas 5 foto", Toast.LENGTH_SHORT).show()
            } else {
                pickImagesLauncher.launch("image/*")
            }
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val stock = binding.etStock.text.toString().toIntOrNull() ?: 0
            val category = binding.spinnerCategory.text.toString()

            if (validateInput(name, category, description, price, stock)) {
                viewModel.saveProduct(
                    name = name,
                    description = description,
                    price = price,
                    stock = stock,
                    categoryName = category,
                    imageUris = selectedImageUris
                )
            }
        }

        observeViewModel()
    }

    private fun setupCategoryDropdown() {
        val categories = arrayOf("Bibit", "Pupuk", "Alat")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_dropdown, categories)
        binding.spinnerCategory.setAdapter(adapter)
    }

    private fun setupStockCounter() {
        binding.btnPlusStock.setOnClickListener {
            val current = binding.etStock.text.toString().toIntOrNull() ?: 0
            binding.etStock.setText((current + 1).toString())
        }

        binding.btnMinusStock.setOnClickListener {
            val current = binding.etStock.text.toString().toIntOrNull() ?: 0
            if (current > 0) {
                binding.etStock.setText((current - 1).toString())
            }
        }
    }

    private fun updateImagePreviews() {
        binding.layoutImagePreviews.removeAllViews()
        binding.tvImageCount.text = if (selectedImageUris.isEmpty()) 
            "Klik untuk pilih foto dari galeri" 
        else "${selectedImageUris.size} foto dipilih"

        val inflater = LayoutInflater.from(requireContext())
        selectedImageUris.forEach { uri ->
            val itemBinding = ItemImagePreviewBinding.inflate(inflater, binding.layoutImagePreviews, false)
            itemBinding.ivPreview.load(uri)
            itemBinding.btnRemove.setOnClickListener {
                selectedImageUris.remove(uri)
                updateImagePreviews()
            }
            binding.layoutImagePreviews.addView(itemBinding.root)
        }
    }

    private fun validateInput(name: String, category: String, desc: String, price: Double, stock: Int): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nama produk wajib diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        if (category.isEmpty()) {
            Toast.makeText(requireContext(), "Pilih kategori produk", Toast.LENGTH_SHORT).show()
            return false
        }
        if (price <= 0) {
            Toast.makeText(requireContext(), "Harga tidak valid", Toast.LENGTH_SHORT).show()
            return false
        }
        if (stock <= 0) {
            Toast.makeText(requireContext(), "Stok minimal 1", Toast.LENGTH_SHORT).show()
            return false
        }
        if (desc.isEmpty()) {
            Toast.makeText(requireContext(), "Deskripsi wajib diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        if (selectedImageUris.isEmpty()) {
            Toast.makeText(requireContext(), "Pilih minimal 1 foto", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.status.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Produk berhasil diposting!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else if (success == false) {
                Toast.makeText(requireContext(), "Gagal memposting produk.", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSave.isEnabled = !isLoading
            binding.btnSave.text = if (isLoading) "Memproses..." else "Publish Produk"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
