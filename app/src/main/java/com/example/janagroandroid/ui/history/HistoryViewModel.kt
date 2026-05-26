package com.example.janagroandroid.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.janagroandroid.data.local.entity.HistoryEntity
import com.example.janagroandroid.data.repository.AppRepository

class HistoryViewModel(
    app: Application,
    private val repo: AppRepository
) : AndroidViewModel(app) {
    val history: LiveData<List<HistoryEntity>> = repo.history
}