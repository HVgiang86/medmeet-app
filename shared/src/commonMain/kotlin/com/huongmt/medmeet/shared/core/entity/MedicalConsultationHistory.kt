package com.huongmt.medmeet.shared.core.entity

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class MedicalConsultationHistory(
    val id: String,
    val patientId: String?,
    val doctorId: String?,
    val clinicId: String?,
    val examinationDate: LocalDate?,
    val clinicScheduleId: String?,
    val examinationReason: String?,
    val medicalFee: Long?,
    val medicalServiceName: String?,
    val paymentMethod: PaymentMethod? = PaymentMethod.CASH,
    val paymentStatus: PaymentStatus? = PaymentStatus.SUCCESS,
    val status: MedicalRecordStatus? = MedicalRecordStatus.PENDING,
    val patientName: String?,
    val patientGender: Gender? = Gender.MALE,
    val patientPhoneNumber: String?,
    val patientEmail: String?,
    val patientDateOfBirth: LocalDate?,
    val patientProvince: String?,
    val patientDistrict: String?,
    val patientCommune: String?,
    val patientAddress: String?,
    val code: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val diagnosis: String?,
    val noteFromDoctor: String?,
    val patientStatus: String?,
    val clinic: Clinic,
    val clinicSchedule: ClinicSchedule
)
