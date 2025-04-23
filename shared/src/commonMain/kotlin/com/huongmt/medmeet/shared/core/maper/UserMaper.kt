package com.huongmt.medmeet.shared.core.maper

import com.huongmt.medmeet.shared.core.datasource.network.response.ProfileResponse
import com.huongmt.medmeet.shared.core.entity.Gender
import com.huongmt.medmeet.shared.core.entity.User
import com.huongmt.medmeet.shared.utils.ext.toLocalDate
import kotlinx.datetime.LocalDate

fun ProfileResponse.toUser(): User {
    val userGender = when (gender) {
        0 -> Gender.MALE
        1 -> Gender.FEMALE
        else -> Gender.OTHER
    }

    val dob: LocalDate? = birthday.toLocalDate()

    return User(
        id = id,
        name = name,
        email = email,
        gender = userGender,
        role = role,
        birthday = dob,
        avatar = avatar,
        province = province,
        district = district,
        address = address,
        code = code
    )
}
