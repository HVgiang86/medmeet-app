package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalService
import kotlinx.coroutines.flow.Flow

interface MedicalRepository {
    suspend fun getMedicalConsultations(): Flow<List<MedicalConsultationHistory>>
    suspend fun getMedicalServices(clinicId: String): Flow<List<MedicalService>>
    suspend fun cancelAppointment(appointmentId: String): Flow<String>
}

class MedicalRepositoryImpl(
    private val api: APIs
) : MedicalRepository, BaseRepository() {

    override suspend fun getMedicalConsultations(): Flow<List<MedicalConsultationHistory>> =
        flowContext(mapper = {
            it.items?.map { record ->
                record.toMedicalHistory()
            } ?: emptyList()
        }) {
            api.getMedicalHistory(WholeApp.USER?.id ?: "")
        }

    override suspend fun getMedicalServices(clinicId: String): Flow<List<MedicalService>> =
        flowContext(mapper = {
            it.items?.map { service ->
                service.toMedicalService()
            } ?: emptyList()
        }) {
            api.getMedicalServices(clinicId)
        }

    override suspend fun cancelAppointment(appointmentId: String): Flow<String> =
        flowContext(mapper = {
            it.id
        }) {
            api.cancelAppointment(appointmentId)
        }
}
