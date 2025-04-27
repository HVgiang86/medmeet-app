package com.huongmt.medmeet.shared.core.repository

import com.huongmt.medmeet.shared.base.BaseRepository
import com.huongmt.medmeet.shared.core.datasource.network.APIs
import com.huongmt.medmeet.shared.core.entity.ActiveStatus
import com.huongmt.medmeet.shared.core.entity.Clinic
import com.huongmt.medmeet.shared.core.entity.ClinicSchedule
import com.huongmt.medmeet.shared.core.maper.toClinic
import kotlinx.coroutines.flow.Flow

interface ClinicRepository {
    suspend fun getAllClinics(
        name: String? = null,
        address: String? = null,
        status: ActiveStatus? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): Flow<List<Clinic>>

    suspend fun getClinicById(id: String): Flow<Clinic>

    suspend fun getClinicScheduleByClinicId(clinicId: String): Flow<List<ClinicSchedule>>
}

class ClinicRepositoryImpl(
    private val api: APIs
) : ClinicRepository, BaseRepository() {
    override suspend fun getAllClinics(
        name: String?,
        address: String?,
        status: ActiveStatus?,
        page: Int?,
        pageSize: Int?
    ): Flow<List<Clinic>> = flowContext(mapper = {
        // Map the response to a list of Clinic objects
        it.items.map { clinicResponse ->
            clinicResponse.toClinic()
        }
    }) {
        api.getClinics(
            name = name,
            address = address,
            status = status?.value,
            page = page,
            pageSize = pageSize
        )
    }

    override suspend fun getClinicById(id: String): Flow<Clinic> = flowContext(mapper = {
        it.toClinic()
    }) {
        api.getClinicById(id)
    }

    override suspend fun getClinicScheduleByClinicId(clinicId: String): Flow<List<ClinicSchedule>> =
        flowContext(mapper = {
            it.map { clinicScheduleResponse ->
                clinicScheduleResponse.toClinicSchedule()
            }
        }) {
            api.getClinicSchedule(
                clinicId = clinicId
            )
        }
}
