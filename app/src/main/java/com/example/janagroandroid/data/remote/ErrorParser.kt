package com.example.janagroandroid.data.remote

import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorParser {
    private val moshi = Moshi.Builder()
        .add(FlexibleStringListAdapter)
        .build()

    fun parse(throwable: Throwable): String {
        return when (throwable) {
            is UnknownHostException, is ConnectException -> {
                "Koneksi internet terputus. Pastikan perangkat Anda terhubung ke internet."
            }
            is SocketTimeoutException -> {
                "Koneksi timeout. Silakan periksa jaringan internet Anda dan coba lagi."
            }
            is IOException -> {
                "Gagal terhubung ke server. Silakan coba beberapa saat lagi."
            }
            is HttpException -> {
                val errorBody = throwable.response()?.errorBody()?.string()
                val parsedMessage = parseErrorBody(errorBody)
                if (parsedMessage != null) {
                    translateErrorMessage(parsedMessage)
                } else {
                    when (throwable.code()) {
                        400 -> "Permintaan tidak valid (Bad Request)."
                        401 -> "Sesi telah berakhir atau kredensial salah. Silakan login kembali."
                        403 -> "Akses ditolak. Akun Anda mungkin telah ditangguhkan."
                        404 -> "Layanan atau data tidak ditemukan."
                        422 -> "Data yang dikirim tidak valid. Silakan periksa kembali inputan Anda."
                        429 -> "Terlalu banyak permintaan. Silakan tunggu beberapa saat lagi."
                        500 -> "Terjadi kesalahan internal pada server. Silakan coba lagi nanti."
                        503 -> "Layanan sedang dalam pemeliharaan. Silakan coba lagi nanti."
                        else -> "Terjadi kesalahan pada server (${throwable.code()})."
                    }
                }
            }
            else -> throwable.localizedMessage ?: "Terjadi kesalahan tidak terduga."
        }
    }

    fun parseErrorBody(body: String?): String? {
        body ?: return null
        if (body.isBlank()) return null
        
        return try {
            val jsonAdapter = moshi.adapter(Any::class.java)
            val json = jsonAdapter.fromJson(body)
            
            when (json) {
                is Map<*, *> -> {
                    // Coba ambil field "message"
                    val message = json["message"]
                    when (message) {
                        is String -> message
                        is List<*> -> message.firstOrNull()?.toString()
                        is Map<*, *> -> {
                            // Coba ambil pesan pertama dari validasi error
                            message.values.firstOrNull()?.let {
                                if (it is List<*>) it.firstOrNull()?.toString() else it.toString()
                            }
                        }
                        else -> json["errors"]?.let { errors ->
                            if (errors is Map<*, *>) errors.values.firstOrNull()?.toString() else null
                        }
                    }
                }
                is List<*> -> json.firstOrNull()?.toString()
                is String -> json
                else -> null
            }
        } catch (e: Exception) {
            // Fallback manual jika parsing Moshi gagal
            try {
                val key = "\"message\":\""
                val msgStart = body.indexOf(key)
                if (msgStart >= 0) {
                    val start = msgStart + key.length
                    val end = body.indexOf("\"", start)
                    if (end > start) body.substring(start, end) else null
                } else null
            } catch (ex: Exception) {
                null
            }
        }
    }

    fun translateErrorMessage(message: String): String {
        val lowerMsg = message.lowercase()
        return when {
            lowerMsg.contains("banned") -> 
                "Akun Anda telah ditangguhkan (banned). Silakan hubungi dukungan pelanggan."
            lowerMsg.contains("incorrect email or password") || lowerMsg.contains("invalid credentials") ->
                "Email atau password salah. Silakan coba lagi."
            lowerMsg.contains("email already in use") || lowerMsg.contains("email already registered") || lowerMsg.contains("email must be unique") ->
                "Email sudah terdaftar. Silakan gunakan email lain."
            lowerMsg.contains("password too short") ->
                "Password terlalu pendek. Minimal 8 karakter."
            lowerMsg.contains("password do not match") || lowerMsg.contains("confirm password") ->
                "Konfirmasi password tidak cocok."
            lowerMsg.contains("token expired") || lowerMsg.contains("jwt expired") ->
                "Sesi Anda telah berakhir. Silakan login kembali."
            lowerMsg.contains("insufficient stock") ->
                "Stok produk tidak mencukupi."
            lowerMsg.contains("already become a seller") ->
                "Anda sudah terdaftar sebagai penjual."
            lowerMsg.contains("not authorized") || lowerMsg.contains("permission denied") ->
                "Anda tidak memiliki izin untuk melakukan aksi ini."
            else -> message
        }
    }
}
