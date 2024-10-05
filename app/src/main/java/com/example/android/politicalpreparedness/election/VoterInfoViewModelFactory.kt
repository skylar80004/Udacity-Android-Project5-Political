package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.data.VoterInfoDataSource
import com.example.android.politicalpreparedness.database.ElectionDao

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource

class VoterInfoViewModelFactory(
    private val voterInfoDataSource: VoterInfoDataSource,
    private val electionDao: ElectionDao
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(
                voterInfoDataSource = voterInfoDataSource,
                electionDao = electionDao
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}