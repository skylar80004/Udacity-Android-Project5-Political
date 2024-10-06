package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.VoterInfoDataSource
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.util.BaseViewModel
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val voterInfoDataSource: VoterInfoDataSource,
    private val electionDao: ElectionDao
) : BaseViewModel() {
    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election> = _election

    val electionDay: LiveData<String> = election.map {
        it.electionDay.toString()
    }

//    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
//    val voterInfo: LiveData<VoterInfoResponse> = _voterInfo

    private val _correspondenceAddress = MutableLiveData<String>()
    val correspondenceAddress: LiveData<String> = _correspondenceAddress

    private val _pollingLocationsURL = MutableLiveData<String>()
    val pollingLocationsURL: LiveData<String> = _pollingLocationsURL

    private val _ballotInfoUrl = MutableLiveData<String>()
    val ballotInfoUrl: LiveData<String> = _ballotInfoUrl

    private val electionId = MutableLiveData<Int>()

    val electionButtonState: LiveData<FollowState> = electionId
        .switchMap { id ->
            electionDao.getElectionById(id)
        }
        .map { election ->
            if (election != null) {
                FollowState.FOLLOWING
            } else {
                FollowState.NOT_FOLLOWING
            }
        }

    fun loadData(electionIdValue: Int) {
        viewModelScope.launch {
            showLoading()
            electionId.postValue(electionIdValue)
            getVoterInfo(electionId = electionIdValue)
            hideLoading()
        }
    }

    private suspend fun getVoterInfo(electionId: Int) {
        val address =
            "340 Main St. Venice CA" // TODO fetch this address from current device address, ask location permissions
        val voterInfoResult = voterInfoDataSource.getVoterInfo(
            address = address,
            electionId = electionId.toString()
        )

        when (voterInfoResult) {
            is DataResult.Success -> {
                println("prueba, voter info result success")

                val voterInfoResponse = voterInfoResult.data
                println("prueba, election name: ${voterInfoResponse.election.name}")

                // _voterInfo.postValue(voterInfoResponse) TODO
                _election.postValue(voterInfoResponse.election)

                val correspondenceAddress =
                    getCorrespondenceAddress(voterInfoResponse = voterInfoResponse)
                if (correspondenceAddress.isNotEmpty()) {
                    _correspondenceAddress.postValue(correspondenceAddress)
                }

                val pollingLocationsUrl =
                    getPollingLocationsUrl(voterInfoResponse = voterInfoResponse)
                if (pollingLocationsUrl.isNotEmpty()) {
                    _pollingLocationsURL.postValue(pollingLocationsUrl)
                }

                val ballotInfoUrl =
                    getBallotInformationUrl(voterInfoResponse = voterInfoResponse)
                if (ballotInfoUrl.isNotEmpty()) {
                    _ballotInfoUrl.postValue(ballotInfoUrl)
                }
            }

            is DataResult.Error -> {
                println("prueba, voter info error: ${voterInfoResult.exception.message}")
                showErrorMessage(message = voterInfoResult.exception.message ?: "")
            }
        }
    }


    private fun getCorrespondenceAddress(voterInfoResponse: VoterInfoResponse): String {
        return voterInfoResponse.state?.firstOrNull()
            ?.electionAdministrationBody?.correspondenceAddress?.let { address ->
                "${address.line1} , ${address.city} , ${address.state}, ${address.zip}"
            } ?: ""
    }

    private fun getPollingLocationsUrl(voterInfoResponse: VoterInfoResponse): String {
        return voterInfoResponse.state?.firstOrNull()
            ?.electionAdministrationBody?.votingLocationFinderUrl
            ?: ""
    }

    private fun getBallotInformationUrl(voterInfoResponse: VoterInfoResponse): String {
        return voterInfoResponse.state?.firstOrNull()?.electionAdministrationBody?.ballotInfoUrl
            ?: ""
    }


    fun onClickElectionButton() {
        viewModelScope.launch {
            val state = electionButtonState.value
            val electionValue = election.value
            when (state) {
                FollowState.FOLLOWING -> {
                    electionValue?.let { election ->
                        electionDao.deleteElection(election)
                    }
                }

                FollowState.NOT_FOLLOWING -> {
                    electionValue?.let { election ->
                        electionDao.insert(election)
                    }
                }

                else -> {

                }
            }
        }
    }

    fun setElectionButtonState(state: FollowState) {
        electionButtonState

    }


//TODO: Add var and methods to save and remove elections to local database
//TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}

enum class FollowState {
    FOLLOWING,
    NOT_FOLLOWING
}