package com.example.android.politicalpreparedness.data

import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse

class RepresentativeNetworkRepository(private val apiService: CivicsApiService) :
    RepresentativeDataSource {
    override suspend fun getRepresentativesByAddress(address: String): DataResult<RepresentativeResponse> {
        return try {
            val response = apiService.getRepresentatives(address = address)

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