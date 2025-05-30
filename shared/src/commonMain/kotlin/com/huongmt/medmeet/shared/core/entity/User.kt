package com.huongmt.medmeet.shared.core.entity

import com.huongmt.medmeet.shared.utils.ext.nowDate
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String? = "",
    val code: String? = null,
    val id: String,
    val name: String? = "",
    val avatar: String? = "",
    val gender: Gender = Gender.MALE,
    val birthday: LocalDate? = null,
    val phoneNumber: String? = "",
    val province: String? = "",
    val district: String? = "",
    val commune: String? = "",
    val address: String? = "",
    val specialty: String? = "",
    val description: String? = "",
    val qualification: String? = "",
    val role: Int = 0
) {
    fun compareTo(other: User): Boolean {
        val isSameDate = this.birthday?.year == other.birthday?.year &&
                this.birthday?.monthNumber == other.birthday?.monthNumber &&
                this.birthday?.dayOfMonth == other.birthday?.dayOfMonth

        return this.name == other.name &&
                this.avatar == other.avatar &&
                this.code == other.code &&
                this.id == other.id &&
                this.email == other.email &&
                this.gender.value == other.gender.value &&
                isSameDate &&
                this.phoneNumber == other.phoneNumber &&
                this.province == other.province &&
                this.district == other.district &&
                this.commune == other.commune &&
                this.address == other.address &&
                this.specialty == other.specialty &&
                this.description == other.description &&
                this.qualification == other.qualification
    }

    fun toDateUpdate(): UpdateProfileData {
        return UpdateProfileData(
            name = name ?: "",
            dob = birthday ?: nowDate(),
            gender = gender,
            province = province ?: "",
            district = district ?: "",
            commune = commune ?: "",
            address = address ?: "",
            phoneNumber = phoneNumber ?: "",
        )

    }
}
