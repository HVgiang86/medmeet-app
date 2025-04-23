package com.huongmt.medmeet.shared.core.maper

import com.huongmt.medmeet.shared.core.datasource.network.response.ClinicResponse
import com.huongmt.medmeet.shared.core.entity.ActiveStatus
import com.huongmt.medmeet.shared.core.entity.Clinic

fun ClinicResponse.toClinic(): Clinic {
    // get status from data.status
    val statusData = when (status) {
        ActiveStatus.ACTIVE.value -> ActiveStatus.ACTIVE
        ActiveStatus.INACTIVE.value -> ActiveStatus.INACTIVE
        else -> ActiveStatus.INACTIVE
    }

    return Clinic(
        id = id,
        name = name,
        email = email,
        hotline = hotline,
        address = address,
        status = statusData,
        description = description,
        logo = logo
    )
}
