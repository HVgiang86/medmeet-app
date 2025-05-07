package com.huongmt.medmeet.shared.core.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HealthRecord(
    @SerialName("_id") val id: String,
    val bloodType: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val healthHistory: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    fun getBloodType(): BloodType {
        if (bloodType.isNullOrEmpty()) return BloodType.NA
        val type = bloodType.trim().lowercase().replace("nhóm máu", "").dropWhile { it == ' ' }
        return when (type) {
            BloodType.A_POS.value -> BloodType.A_POS
            BloodType.AB_NEG.value -> BloodType.A_NEG
            BloodType.B_POS.value -> BloodType.B_POS
            BloodType.B_NEG.value -> BloodType.B_NEG
            BloodType.AB_POS.value -> BloodType.AB_POS
            BloodType.AB_NEG.value -> BloodType.AB_NEG
            BloodType.O_POS.value -> BloodType.O_POS
            BloodType.O_NEG.value -> BloodType.O_NEG
            "a" -> BloodType.A_NEG
            "b" -> BloodType.B_NEG
            "ab" -> BloodType.AB_NEG
            "o" -> BloodType.O_NEG
            else -> {
                if (type.contains("a-")) {
                    BloodType.A_NEG
                } else if (type.contains("b-")) {
                    BloodType.B_NEG
                } else if (type.contains("ab-")) {
                    BloodType.AB_NEG
                } else if (type.contains("o-")) {
                    BloodType.O_NEG
                } else if (type.contains("a+")) {
                    BloodType.A_POS
                } else if (type.contains("b+")) {
                    BloodType.B_POS
                } else if (type.contains("ab+")) {
                    BloodType.AB_POS
                } else if (type.contains("o+")) {
                    BloodType.O_POS
                } else {
                    BloodType.NA
                }
            }
        }
    }
}
