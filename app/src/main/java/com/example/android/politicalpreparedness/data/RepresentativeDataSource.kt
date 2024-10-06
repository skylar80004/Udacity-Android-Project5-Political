package com.example.android.politicalpreparedness.data

import com.example.android.politicalpreparedness.network.DataResult
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse

interface RepresentativeDataSource {
    suspend fun getRepresentativesByAddress(
        address: String,
    ): DataResult<RepresentativeResponse>
}