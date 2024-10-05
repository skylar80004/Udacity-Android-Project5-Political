package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.VoterInfoDataSource
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.BaseViewModel
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val electionLocalDataSource: ElectionDao,
    private val voterInfoDataSource: VoterInfoDataSource
) : BaseViewModel() {
    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election> = _election

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse> = _voterInfo

    fun fetchVoterInfo(electionId: Int) {
        viewModelScope.launch {
            showLoading()

            val address = "340 Main St. Venice CA"
            val voterInfoResult = voterInfoDataSource.getVoterInfo(address = address, electionId = electionId.toString())

            when (voterInfoResult) {
                is DataResult.Success -> {
                    println("prueba, voter info result success")

                    val voterInfo = voterInfoResult.data
                    println("prueba, election name: ${voterInfo.election.name}")

                    _voterInfo.postValue(voterInfo)
                    _election.postValue(voterInfo.election)
                }
                is DataResult.Error -> {
                    println("prueba, voter info error: ${voterInfoResult.exception.message}")
                    showErrorMessage(message = voterInfoResult.exception.message ?: "")
                }
            }
            hideLoading()
        }
    }



//TODO: Add live data to hold voter info

//TODO: Add var and methods to populate voter info

//TODO: Add var and methods to support loading URLs

//TODO: Add var and methods to save and remove elections to local database
//TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}