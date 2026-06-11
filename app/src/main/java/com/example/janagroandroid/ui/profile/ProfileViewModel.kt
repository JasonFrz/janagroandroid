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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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

    init {
        refreshProfile()
    }

    fun refreshProfile() {
        viewModelScope.launch {
            repo.refreshProfile()
        }
    }

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

    // Fungsi untuk memperbarui nama dan nomor telepon
    fun updateProfileInfo(name: String, phone: String) {
        _profileUpdateStatus.value = ProfileUpdateStatus.Loading
        viewModelScope.launch {
            try {
                val success = repo.updateProfile(name = name, phone = phone)
                if (success) {
                    _profileUpdateStatus.postValue(ProfileUpdateStatus.Success)
                    repo.refreshProfile()
                } else {
                    _profileUpdateStatus.postValue(ProfileUpdateStatus.Error("Gagal memperbarui profil"))
                }
            } catch (e: Exception) {
                _profileUpdateStatus.postValue(ProfileUpdateStatus.Error(e.message ?: "Unknown error"))
            }
        }
    }

    // Fungsi untuk memperbarui foto profil
    fun updateProfilePicture(imageUri: Uri) {
        _profileUpdateStatus.value = ProfileUpdateStatus.Loading
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val contentResolver = context.contentResolver
                
                // Mengambil input stream dari Uri
                val inputStream = contentResolver.openInputStream(imageUri)
                val byteArray = inputStream?.readBytes()
                inputStream?.close()

                if (byteArray != null) {
                    // Membuat RequestBody dari byte array
                    val contentType = contentResolver.getType(imageUri) ?: "image/jpeg"
                    val requestFile = byteArray.toRequestBody(
                        contentType.toMediaTypeOrNull(),
                        0,
                        byteArray.size
                    )

                    // 'profile_picture' harus sesuai dengan nama field di multer backend Anda (uploadProfile.single('profile_picture'))
                    val body = MultipartBody.Part.createFormData(
                        "profile_picture",
                        "profile_${System.currentTimeMillis()}.jpg",
                        requestFile
                    )

                    val success = repo.updateProfile(imagePart = body)
                    if (success) {
                        _profileUpdateStatus.postValue(ProfileUpdateStatus.Success)
                        // Refresh data setelah berhasil update
                        repo.refreshProfile()
                    } else {
                        _profileUpdateStatus.postValue(ProfileUpdateStatus.Error("Gagal memperbarui profil di server. Pastikan endpoint benar."))
                    }
                } else {
                    _profileUpdateStatus.postValue(ProfileUpdateStatus.Error("Gagal membaca file gambar"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
