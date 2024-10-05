package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.data.ElectionDataSource
import com.example.android.politicalpreparedness.data.VoterInfoDataSource
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import java.util.Date

class ElectionsViewModel(
    private val electionsDataSource: ElectionDataSource,
    private val electionDao: ElectionDao
) : ViewModel() {
    // TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>> = _upcomingElections

    // TODO: Create live data val for saved elections
    val savedElections: LiveData<List<Election>> = electionDao.getAllElections()

    private val _showError = MutableLiveData<String>()
    val showError: LiveData<String> = _showError

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    fun loadUpcomingElections() {
        viewModelScope.launch {
            showLoading()
            when (val electionsResult = electionsDataSource.getElections()) {
                is DataResult.Success -> {
                    println("prueba, success: $${electionsResult.data.elections}")
                    _upcomingElections.postValue(electionsResult.data.elections)
                }

                is DataResult.Error -> {
                    println("prueba, error: ${electionsResult.exception.message}")

                    _showError.postValue(electionsResult.exception.message)

                }
            }
            hideLoading()
        }
    }

    private fun showLoading() {
        _showLoading.postValue(true)
    }
    private fun hideLoading() {
        _showLoading.postValue(false)
    }



    private fun loadUpcomingElectionsDebug() {
        // Hardcoded list of 5 election instances
        val elections = listOf(
            Election(
                id = 1,
                name = "Presidential Election",
                electionDay = Date(),
                division = Division(
                    "division_1",
                    "USA",
                    "California"
                ) // Adjusted for your Division class definition
            ),
            Election(
                id = 2,
                name = "Congressional Election",
                electionDay = Date(),
                division = Division("division_2", "USA", "Texas")
            ),
            Election(
                id = 3,
                name = "Gubernatorial Election",
                electionDay = Date(),
                division = Division("division_3", "USA", "New York")
            ),
            Election(
                id = 4,
                name = "Senatorial Election",
                electionDay = Date(),
                division = Division("division_4", "USA", "Florida")
            ),
            Election(
                id = 5,
                name = "Local Election",
                electionDay = Date(),
                division = Division("division_5", "USA", "Illinois")
            )
        )

        _upcomingElections.value = elections
    }
    // TODO: Create functions to navigate to saved or upcoming election voter info
}
