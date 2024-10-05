package com.example.android.politicalpreparedness.data

import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

class ElectionsNetworkRepository(private val apiService: CivicsApiService) : ElectionDataSource {
    override suspend fun getElections(): DataResult<ElectionResponse> {
        return try {
            val response = apiService.getElections()
            if (response.isSuccessful) {
                response.body()?.let { DataResult.Success(it) }
                    ?: DataResult.Error(Exception("No data found"))
            } else {
                DataResult.Error(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun getVoterInfo(
        address: String,
        electionId: String
    ): DataResult<VoterInfoResponse> {
        return try {
            val response = apiService.getVoterInfo(address, electionId)
            if (response.isSuccessful) {
                response.body()?.let { DataResult.Success(it) }
                    ?: DataResult.Error(Exception("No data found"))
            } else {
                DataResult.Error(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun getRepresentatives(address: String): DataResult<RepresentativeResponse> {
        return try {
            val response = apiService.getRepresentatives(address)
            if (response.isSuccessful) {
                response.body()?.let { DataResult.Success(it) }
                    ?: DataResult.Error(Exception("No data found"))
            } else {
                DataResult.Error(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
