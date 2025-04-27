package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.MedicalService
import com.huongmt.medmeet.shared.core.entity.MedicalServiceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagingMedicalServiceResponse(
    val meta: PaginationMeta?,
    val items: List<MedicalServiceResponse>? = emptyList()
)

@Serializable
data class MedicalServiceResponse(
    @SerialName("_id") val id: String,
    val name: String? = "",
    val currentPrice: Long? = 0,
    val originalPrice: Long? = 0,
    val type: Int = MedicalServiceType.SPECIALITY.value,
    val clinicId: String
) {
    fun toMedicalService(): MedicalService {
        return MedicalService(
            id = id,
            name = name,
            currentPrice = currentPrice,
            originalPrice = originalPrice,
            type = MedicalServiceType.entries.firstOrNull { it.value == type }
                ?: MedicalServiceType.SPECIALITY,
            clinicId = clinicId
        )
    }
}
