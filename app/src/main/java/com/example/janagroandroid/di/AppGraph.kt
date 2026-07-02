package com.example.janagroandroid.di

import android.content.Context
import com.example.janagroandroid.data.local.AppDatabase
import com.example.janagroandroid.data.local.SessionManager
import com.example.janagroandroid.data.remote.GlobalAuthHandler
import com.example.janagroandroid.data.remote.RetrofitClient
import com.example.janagroandroid.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppGraph {
    @Volatile
    private var repositoryInstance: AppRepository? = null

    fun repository(context: Context): AppRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: createRepository(context).also { repositoryInstance = it }
        }
    }

    private fun createRepository(context: Context): AppRepository {
        val db = AppDatabase.getInstance(context)
        val sessionManager = SessionManager(context)

        GlobalAuthHandler.init {
            sessionManager.clear()
            CoroutineScope(Dispatchers.IO).launch {
                db.userDao().logoutAll()
            }
        }

        return AppRepository(
            userDao = db.userDao(),
            cartDao = db.cartDao(),
            historyDao = db.historyDao(),
            apiService = RetrofitClient.getApiService(sessionManager),
            sessionManager = sessionManager,
            productDao = db.productDao()
        )
    }
}