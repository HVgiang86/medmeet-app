package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface MedicalConsultationRepository {
    suspend fun getMedicalConsultations(): Flow<List<MedicalConsultationHistory>>
}

class MedicalConsultationRepositoryImpl(
    private val api: APIs
) : MedicalConsultationRepository, BaseRepository() {

    override suspend fun getMedicalConsultations(): Flow<List<MedicalConsultationHistory>> = flowContext(mapper = {
        it.items?.map { record ->
            record.toMedicalHistory()
        } ?: emptyList()
    }) {
        api.getMedicalHistory(WholeApp.USER?.id ?: "")
    }
}
