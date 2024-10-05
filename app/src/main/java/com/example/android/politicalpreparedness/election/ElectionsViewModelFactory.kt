package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.ElectionDataSource

class ElectionsViewModelFactory(
    private val electionsDataSource: ElectionDataSource
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(electionsDataSource = electionsDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}