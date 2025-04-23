package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.ActiveStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClinicResponse(
    @SerialName("_id") val id: String,
    val name: String? = "",
    val email: String? = "",
    val hotline: String? = "",
    val address: String? = "",
    val status: Int? = ActiveStatus.ACTIVE.value,
    val description: String? = "",
    val logo: String? = ""
)

@Serializable
data class ClinicListResponse(
    val items: List<ClinicResponse> = emptyList(),
    val meta: PaginationMeta? = null
)

@Serializable
data class PaginationMeta(
    val total: Int? = 0,
    val page: Int? = 1
)
