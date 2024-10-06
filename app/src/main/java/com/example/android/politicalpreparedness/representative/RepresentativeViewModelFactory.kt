package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.RepresentativeDataSource

class RepresentativeViewModelFactory(
    private val representativeDataSource: RepresentativeDataSource
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            return RepresentativeViewModel(
                representativeDataSource = representativeDataSource
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}