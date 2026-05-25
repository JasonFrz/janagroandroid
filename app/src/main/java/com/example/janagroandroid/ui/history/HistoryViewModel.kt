package com.example.janagroandroid.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.janagroandroid.data.local.entity.TransactionEntity
import com.example.janagroandroid.data.repository.AppRepository

class HistoryViewModel(
    private val repo: AppRepository
) : ViewModel() {

    val history: LiveData<List<TransactionEntity>> = repo.history
}