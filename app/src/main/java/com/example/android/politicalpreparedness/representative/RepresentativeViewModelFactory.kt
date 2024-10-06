package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.android.politicalpreparedness.data.RepresentativeDataSource

class RepresentativeViewModelFactory(
    private val representativeDataSource: RepresentativeDataSource
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return RepresentativeViewModel(
                representativeDataSource = representativeDataSource,
                savedStateHandle = savedStateHandle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}