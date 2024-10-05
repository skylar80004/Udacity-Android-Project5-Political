package com.example.android.politicalpreparedness.data

import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface VoterInfoDataSource {
    suspend fun getVoterInfo(address: String, electionId: String): DataResult<VoterInfoResponse>
}