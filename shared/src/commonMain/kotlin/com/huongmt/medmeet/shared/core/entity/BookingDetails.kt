package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDate

data class BookingDetails(
    val patientInfo: PatientInfo?,
    val clinic: Clinic?,
    val medicalService: MedicalService?,
    val examinationDate: LocalDate?,
    val clinicSchedule: ClinicSchedule?,
    val clinicName: String?,
    val paymentMethod: PaymentMethod?
)

data class PatientInfo(
    val name: String? = "",
    val phoneNumber: String? = "",
    val email: String? = "",
    val gender: Gender? = Gender.MALE,
    val dateOfBirth: LocalDate? = null,
    val province: String? = "",
    val district: String? = "",
    val commune: String? = "",
    val address: String? = "",
    val examinationReason: String? = ""
)
