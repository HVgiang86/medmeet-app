package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.entity.BloodType
import com.huongmt.medmeet.shared.core.entity.HealthRecord
import kotlinx.coroutines.flow.Flow

interface HealthRecordRepository {
    suspend fun getHealthRecord(userId: String): Flow<HealthRecord>
    suspend fun updateHealthRecord(
        userId: String,
        bloodType: BloodType?,
        height: Int?,
        weight: Int?,
        healthHistory: String?
    ): Flow<HealthRecord>
}

class HealthRecordRepositoryImpl(
    private val api: APIs
) : HealthRecordRepository, BaseRepository() {

    override suspend fun getHealthRecord(userId: String): Flow<HealthRecord> =
        flowContext(mapper = { response ->
            HealthRecord(
                id = response.id,
                bloodType = response.bloodType,
                height = response.height,
                weight = response.weight,
                healthHistory = response.healthHistory,
                createdAt = response.createdAt,
                updatedAt = response.updatedAt
            )
        }) {
            api.getHealthRecord(userId)
        }

    override suspend fun updateHealthRecord(
        userId: String,
        bloodType: BloodType?,
        height: Int?,
        weight: Int?,
        healthHistory: String?
    ): Flow<HealthRecord> {
        val bloodTypeParam = bloodType?.text ?: ""
        val heightParam = height ?: 0
        val weightParam = weight ?: 0
        val healthHistoryParam = healthHistory ?: ""

        println("REPO: bloodType: $bloodTypeParam height: $heightParam weight: $weightParam healthHistory: $healthHistoryParam")

        return flowContext(mapper = { response ->
            HealthRecord(
                id = response.id,
                bloodType = response.bloodType,
                height = response.height,
                weight = response.weight,
                healthHistory = response.healthHistory,
                createdAt = response.createdAt,
                updatedAt = response.updatedAt
            )
        }) {
            api.updateHealthRecord(
                id = userId,
                bloodType = bloodTypeParam,
                height = heightParam,
                weight = weightParam,
                healthHistory = healthHistoryParam
            )
        }
    }
}
