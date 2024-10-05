package com.example.android.politicalpreparedness.data

import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface ElectionDataSource {
    suspend fun getElections(): DataResult<ElectionResponse>
    suspend fun getVoterInfo(address: String, electionId: String):  DataResult<VoterInfoResponse>
    suspend fun getRepresentatives(address: String):  DataResult<RepresentativeResponse>
}