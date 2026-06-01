package com.example.janagroandroid.data.remote

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object GlobalAuthHandler {
    private var onLogout: (() -> Unit)? = null

    fun init(logoutAction: () -> Unit) {
        onLogout = logoutAction
    }

    fun logout() {
        onLogout?.invoke()
    }
}
