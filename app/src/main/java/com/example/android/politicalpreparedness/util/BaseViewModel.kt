package com.example.android.politicalpreparedness.util

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()

    fun showLoading() {
        showLoading.postValue(true)
    }

    fun hideLoading() {
        showLoading.postValue(false)
    }

    fun showErrorMessage(message: String) {
        showErrorMessage.postValue(message)
    }
}