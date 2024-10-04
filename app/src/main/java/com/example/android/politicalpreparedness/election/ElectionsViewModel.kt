package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import java.util.Date

class ElectionsViewModel : ViewModel() {
    // TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>> = _upcomingElections

    // TODO: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>> = _savedElections

    // TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    init {
        viewModelScope.launch {
            loadUpcomingElections()
        }
    }

    private fun loadUpcomingElections() {
        // Hardcoded list of 5 election instances
        val elections = listOf(
            Election(
                id = 1,
                name = "Presidential Election",
                electionDay = Date(),
                division = Division("division_1", "USA", "California") // Adjusted for your Division class definition
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
        _savedElections.value = elections
    }
    // TODO: Create functions to navigate to saved or upcoming election voter info
}
