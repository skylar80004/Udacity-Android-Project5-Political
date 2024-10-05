package com.example.android.politicalpreparedness.data

import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlin.enums.enumEntries

class VoterInfoNetworkRepository(private val apiService: CivicsApiService) : VoterInfoDataSource {
    override suspend fun getVoterInfo(
        address: String,
        electionId: String
    ): DataResult<VoterInfoResponse> {
        return try {
            val response = apiService.getVoterInfo(address = address, electionId = electionId)

            if (response.isSuccessful) {
                response.body()?.let {
                    DataResult.Success(it)
                } ?: DataResult.Error(Exception("No data found"))

            } else {
                DataResult.Error(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}