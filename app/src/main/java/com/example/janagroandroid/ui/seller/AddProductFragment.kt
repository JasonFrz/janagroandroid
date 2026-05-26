package com.example.janagroandroid.ui.seller

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.janagroandroid.R
import com.example.janagroandroid.databinding.FragmentAddProductBinding
import com.example.janagroandroid.di.AppGraph
import com.example.janagroandroid.ui.AppViewModelFactory

class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddProductViewModel by viewModels {
        AppViewModelFactory(requireActivity().application, AppGraph.repository(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAddProductBinding.bind(view)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val stock = binding.etStock.text.toString().toIntOrNull() ?: 0
            val imageUrl = binding.etImageUrl.text.toString()
            val description = binding.etDescription.text.toString()
            val category = binding.etCategory.text.toString()

            viewModel.save(
                name = name,
                price = price,
                stock = stock,
                imageUrl = imageUrl,
                description = description,
                category = category
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}