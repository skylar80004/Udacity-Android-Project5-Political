package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.RepresentativeDataSource
import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.BaseViewModel
import kotlinx.coroutines.launch

class RepresentativeViewModel(
    private val representativeDataSource: RepresentativeDataSource,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    companion object {
        private const val REPRESENTATIVES_KEY = "representatives"
    }
    
    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>> = _representatives

    init {
        savedStateHandle.get<List<Representative>>(REPRESENTATIVES_KEY)?.let {
            _representatives.value = it
        }
    }

    fun setSelectedState(value: String) {
        state.postValue(value)
    }

    private fun getFormattedAddressFromUI(): Address {
        // Get values from LiveData and use default empty strings if null
        val line1 = addressLine1.value?.takeIf { it.isNotEmpty() } ?: ""
        val line2 = addressLine2.value?.takeIf { it.isNotEmpty() }?.let { " $it" } ?: ""
        val cityValue = city.value?.takeIf { it.isNotEmpty() } ?: ""
        val stateValue = state.value?.takeIf { it.isNotEmpty() } ?: ""
        val zipValue = zip.value?.takeIf { it.isNotEmpty() } ?: ""

        return Address(
            line1 = line1,
            line2 = line2,
            city = cityValue,
            state = stateValue,
            zip = zipValue

        )
    }

    private fun fetchRepresentatives(address: Address) {
        viewModelScope.launch {
            //val address = "340 Main St. Venice CA" // TODO fetch from location
            showLoading()

            val representativeResult =
                representativeDataSource.getRepresentativesByAddress(address = address.toFormattedString())

            when (representativeResult) {
                is DataResult.Success -> {
                    val representativeResponse = representativeResult.data

                    val representatives: List<Representative> =
                        representativeResponse.offices.flatMap { office ->
                            office.getRepresentatives(representativeResponse.officials)
                        }

                    savedStateHandle[REPRESENTATIVES_KEY] = representatives
                    _representatives.postValue(representatives)
                }

                is DataResult.Error -> {
                    showErrorMessage(representativeResult.exception.message ?: "Unknown error")
                }
            }
            hideLoading()
        }
    }

    fun getRepresentatives(address: Address) {
        addressLine1.postValue(address.line1)
        addressLine2.postValue(address.line2 ?: "")
        city.postValue(address.city)
        state.postValue(address.state)
        zip.postValue(address.zip)

        fetchRepresentatives(address = address)
    }

    fun getRepresentatives() {
        fetchRepresentatives(address = getFormattedAddressFromUI())
    }

}
