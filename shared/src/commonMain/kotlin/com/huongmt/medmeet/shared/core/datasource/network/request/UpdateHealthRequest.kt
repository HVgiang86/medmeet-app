package com.huongmt.medmeet.shared.core.datasource.network.request

import com.huongmt.medmeet.shared.core.entity.BloodType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateHealthRequest(
    val bloodType: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val healthHistory: String? = null
) {
    companion object {
        fun create(
            bloodType: BloodType? = null,
            height: Int? = null,
            weight: Int? = null,
            healthHistory: String? = null
        ): UpdateHealthRequest {
            val bloodTypeParam = if (bloodType == null || bloodType == BloodType.NA) {
                null
            } else {
                bloodType.text
            }

            return UpdateHealthRequest(
                bloodType = bloodTypeParam,
                height = height,
                weight = weight,
                healthHistory = healthHistory
            )
        }
    }
}
