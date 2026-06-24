package com.example.janagroandroid.ui.seller

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddProductViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _status = MutableLiveData<Boolean?>()
    val status: LiveData<Boolean?> = _status

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun saveProduct(
        name: String,
        description: String,
        price: Double,
        stock: Int,
        categoryName: String,
        imageUris: List<Uri>,
        productId: Long = 0L
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                // Map category name to ID (Assume 1: Bibit, 2: Pupuk, 3: Alat based on requirements)
                val categoryId = when (categoryName) {
                    "Bibit" -> 1
                    "Pupuk" -> 2
                    "Alat" -> 3
                    else -> 1
                }

                val imageParts = imageUris.mapNotNull { uri ->
                    prepareFilePart(uri)
                }

                val success = if (productId > 0) {
                    repo.updateRemoteProduct(
                        productId = productId,
                        name = name,
                        description = description,
                        price = price,
                        stock = stock,
                        categoryId = categoryId,
                        imageParts = imageParts
                    )
                } else {
                    repo.createRemoteProduct(
                        name = name,
                        description = description,
                        price = price,
                        stock = stock,
                        categoryId = categoryId,
                        imageParts = imageParts
                    )
                }
                _status.postValue(success)
            } catch (e: Exception) {
                e.printStackTrace()
                _status.postValue(false)
            } finally {
                _loading.postValue(false)
            }
        }
    }

    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part? {
        return try {
            val context = getApplication<Application>().applicationContext
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(fileUri)
            val byteArray = inputStream?.readBytes()
            inputStream?.close()

            byteArray?.let {
                val contentType = contentResolver.getType(fileUri) ?: "image/jpeg"
                val requestFile = it.toRequestBody(
                    contentType.toMediaTypeOrNull(),
                    0,
                    it.size
                )
                MultipartBody.Part.createFormData(
                    "images",
                    "product_${System.currentTimeMillis()}.jpg",
                    requestFile
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
