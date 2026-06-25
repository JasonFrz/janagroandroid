package com.example.janagroandroid.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.remote.dto.AdminProductDto
import com.example.janagroandroid.data.remote.dto.AdminReviewDto
import com.example.janagroandroid.data.remote.dto.AdminUserDto
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class AdminReportsViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    private val _users = MutableLiveData<List<AdminUserDto>>(emptyList())
    val users: LiveData<List<AdminUserDto>> = _users

    private val _products = MutableLiveData<List<AdminProductDto>>(emptyList())
    val products: LiveData<List<AdminProductDto>> = _products

    private val _reviews = MutableLiveData<List<AdminReviewDto>>(emptyList())
    val reviews: LiveData<List<AdminReviewDto>> = _reviews

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // One-shot message for Toast/snackbar feedback.
    private val _message = MutableLiveData<String?>(null)
    val message: LiveData<String?> = _message

    fun consumeMessage() {
        _message.value = null
    }

    fun loadAll() {
        loadUsers()
        loadProducts()
        loadReviews()
    }

    fun loadUsers(search: String? = null) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _users.postValue(repo.getAdminUsers(search = search))
            _isLoading.postValue(false)
        }
    }

    fun loadProducts(search: String? = null) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _products.postValue(repo.getAdminProducts(search))
            _isLoading.postValue(false)
        }
    }

    fun loadReviews() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _reviews.postValue(repo.getAdminReviews())
            _isLoading.postValue(false)
        }
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    fun banUser(id: Long) = setUserStatus(id, "Banned")
    fun unbanUser(id: Long) = setUserStatus(id, "Active")

    private fun setUserStatus(id: Long, status: String) {
        viewModelScope.launch {
            val ok = repo.setUserStatus(id, status)
            _message.postValue(
                if (ok) {
                    if (status == "Banned") "Pengguna berhasil diban" else "Ban pengguna dibuka"
                } else "Gagal mengubah status pengguna"
            )
            if (ok) _users.postValue(repo.getAdminUsers())
        }
    }

    fun deleteUser(id: Long) {
        viewModelScope.launch {
            val ok = repo.adminDeleteUser(id)
            _message.postValue(if (ok) "Pengguna dihapus" else "Gagal menghapus pengguna")
            if (ok) _users.postValue(repo.getAdminUsers())
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            val ok = repo.adminDeleteProduct(id)
            _message.postValue(if (ok) "Produk dihapus" else "Gagal menghapus produk")
            if (ok) _products.postValue(repo.getAdminProducts())
        }
    }

    fun setReviewHidden(id: Long, hidden: Boolean) {
        viewModelScope.launch {
            val ok = repo.setReviewVisibility(id, hidden)
            _message.postValue(
                if (ok) {
                    if (hidden) "Ulasan disembunyikan" else "Ulasan ditampilkan kembali"
                } else "Gagal mengubah ulasan"
            )
            if (ok) _reviews.postValue(repo.getAdminReviews())
        }
    }
}
