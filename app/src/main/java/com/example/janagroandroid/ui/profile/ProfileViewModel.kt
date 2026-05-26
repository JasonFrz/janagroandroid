package com.example.janagroandroid.ui.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.janagroandroid.data.local.entity.UserEntity
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {

    val user: LiveData<UserEntity?> = repo.getUser

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    // LiveData untuk memantau status upload gambar profil
    private val _profileUpdateStatus = MutableLiveData<ProfileUpdateStatus>()
    val profileUpdateStatus: LiveData<ProfileUpdateStatus> get() = _profileUpdateStatus

    fun logout() {
        viewModelScope.launch {
            try {
                repo.logout()
                _logoutSuccess.postValue(true)
            } catch (e: Exception) {
                _logoutSuccess.postValue(true)
            }
        }
    }

    // Fungsi untuk memperbarui foto profil
    fun updateProfilePicture(imageUri: Uri) {
        _profileUpdateStatus.value = ProfileUpdateStatus.Loading
        viewModelScope.launch {
            try {
                // Di sini Anda akan memanggil fungsi repositori untuk mengunggah gambar
                // ke server dan kemudian memperbarui database lokal.
                // repo.uploadProfilePicture(imageUri)

                // Simulasikan keberhasilan untuk saat ini
                _profileUpdateStatus.postValue(ProfileUpdateStatus.Success)
            } catch (e: Exception) {
                _profileUpdateStatus.postValue(ProfileUpdateStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }

    // Sealed class untuk status pembaruan
    sealed class ProfileUpdateStatus {
        object Loading : ProfileUpdateStatus()
        object Success : ProfileUpdateStatus()
        data class Error(val message: String) : ProfileUpdateStatus()
    }
}