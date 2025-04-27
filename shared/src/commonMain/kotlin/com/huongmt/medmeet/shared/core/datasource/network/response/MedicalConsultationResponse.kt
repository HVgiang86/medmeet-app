package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.Gender
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalRecordStatus
import com.huongmt.medmeet.shared.core.entity.PaymentMethod
import com.huongmt.medmeet.shared.core.entity.PaymentStatus
import com.huongmt.medmeet.shared.core.maper.toClinic
import com.huongmt.medmeet.shared.utils.ext.nowDateTime
import com.huongmt.medmeet.shared.utils.ext.toLocalDateFromIso
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagingConsultationResponse(
    val meta: PaginationMeta?,
    val items: List<MedicalConsultationResponse>? = emptyList()
)

@Serializable
data class MedicalConsultationResponse(
    @SerialName("_id")
    val id: String,
    val patientId: String?,
    @SerialName("responsibilityDoctorId")
    val doctorId: String?,
    val clinicId: String?,
    val examinationDate: String?,
    val clinicScheduleId: String?,
    val examinationReason: String?,
    val medicalFee: Long?,
    val medicalServiceName: String?,
    val paymentMethod: Int? = PaymentMethod.CASH.value,
    val paymentStatus: Int? = PaymentStatus.SUCCESS.value,
    val status: Int? = MedicalRecordStatus.PENDING.value,
    val patientName: String?,
    val patientGender: Int? = Gender.MALE.value,
    val patientPhoneNumber: String?,
    val patientEmail: String?,
    val patientDateOfBirth: String?,
    val patientProvince: String?,
    val patientDistrict: String?,
    val patientCommune: String?,
    val patientAddress: String?,
    val code: String,
    val createdAt: String?,
    val updatedAt: String?,
    val diagnosis: String?,
    val noteFromDoctor: String?,
    val patientStatus: String?,
    val clinic: ClinicResponse,
    val clinicSchedule: ClinicScheduleResponse
) {
    fun toMedicalHistory(): MedicalConsultationHistory {
        val now = nowDateTime()

        val createdAtDate = createdAt?.toLocalDateFromIso() ?: now
        val updatedAtDate = updatedAt?.toLocalDateFromIso() ?: now
        val patientDOB = patientDateOfBirth?.toLocalDateFromIso() ?: now
        val examinationDateTime = examinationDate?.toLocalDateFromIso()?.date ?: now.date

        val genderValue: Gender = when (patientGender) {
            Gender.MALE.value -> Gender.MALE
            Gender.FEMALE.value -> Gender.FEMALE
            Gender.OTHER.value -> Gender.OTHER
            else -> Gender.OTHER
        }

        val paymentMethodValue: PaymentMethod = when (paymentMethod) {
            PaymentMethod.CASH.value -> PaymentMethod.CASH
            PaymentMethod.VNPAY.value -> PaymentMethod.VNPAY
            else -> PaymentMethod.CASH
        }

        val paymentStatusValue: PaymentStatus = when (paymentStatus) {
            PaymentStatus.SUCCESS.value -> PaymentStatus.SUCCESS
            PaymentStatus.FAILED.value -> PaymentStatus.FAILED
            else -> PaymentStatus.SUCCESS
        }

        val statusValue: MedicalRecordStatus = when (status) {
            MedicalRecordStatus.PENDING.value -> MedicalRecordStatus.PENDING
            MedicalRecordStatus.CANCELED.value -> MedicalRecordStatus.CANCELED
            MedicalRecordStatus.COMPLETED.value -> MedicalRecordStatus.COMPLETED
            else -> MedicalRecordStatus.PENDING
        }

        return MedicalConsultationHistory(
            id = id,
            patientId = patientId ?: "",
            doctorId = doctorId,
            clinicId = clinicId,
            examinationDate = examinationDateTime,
            clinicScheduleId = clinicScheduleId,
            examinationReason = examinationReason,
            medicalFee = medicalFee,
            medicalServiceName = medicalServiceName,
            paymentMethod = paymentMethodValue,
            paymentStatus = paymentStatusValue,
            status = statusValue,
            patientName = patientName,
            patientGender = genderValue,
            patientPhoneNumber = patientPhoneNumber,
            patientEmail = patientEmail,
            patientDateOfBirth = patientDOB.date,
            patientProvince = patientProvince,
            patientDistrict = patientDistrict,
            patientCommune = patientCommune,
            patientAddress = patientAddress,
            code = code,
            createdAt = createdAtDate,
            updatedAt = updatedAtDate,
            diagnosis = diagnosis,
            noteFromDoctor = noteFromDoctor,
            patientStatus = patientStatus ?: "",
            clinic = clinic.toClinic(),
            clinicSchedule = clinicSchedule.toClinicSchedule()
        )
    }
}
