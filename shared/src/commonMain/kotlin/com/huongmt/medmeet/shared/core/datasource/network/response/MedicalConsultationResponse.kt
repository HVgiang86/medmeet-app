package com.huongmt.medmeet.shared.core.datasource.network.response

import com.huongmt.medmeet.shared.core.entity.Gender
import com.huongmt.medmeet.shared.core.entity.MedicalConsultationHistory
import com.huongmt.medmeet.shared.core.entity.MedicalRecordStatus
import com.huongmt.medmeet.shared.core.entity.PaymentMethod
import com.huongmt.medmeet.shared.core.entity.PaymentStatus
import com.huongmt.medmeet.shared.core.maper.toClinic
import com.huongmt.medmeet.shared.utils.ext.nowDateTime
import com.huongmt.medmeet.shared.utils.ext.plusDate
import com.huongmt.medmeet.shared.utils.ext.toLocalDateFromIso
import kotlinx.datetime.LocalDateTime
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
    val patientId: String? = null,
    @SerialName("responsibilityDoctorId")
    val doctorId: String? = null,
    val clinicId: String? = null,
    val examinationDate: String? = null,
    val clinicScheduleId: String? = null,
    val examinationReason: String? = null,
    val medicalFee: Long? = null,
    val medicalServiceName: String? = null,
    val paymentMethod: Int? = PaymentMethod.CASH.value,
    val paymentStatus: Int? = PaymentStatus.SUCCESS.value,
    val status: Int? = MedicalRecordStatus.PENDING.value,
    val patientName: String? = null,
    val patientGender: Int? = Gender.MALE.value,
    val patientPhoneNumber: String? = null,
    val patientEmail: String? = null,
    val patientDateOfBirth: String? = null,
    val patientProvince: String? = null,
    val patientDistrict: String? = null,
    val patientCommune: String? = null,
    val patientAddress: String? = null,
    val code: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val diagnosis: String? = null,
    val noteFromDoctor: String? = null,
    val patientStatus: String? = null,
    val clinic: ClinicResponse,
    val clinicSchedule: ClinicScheduleResponse
) {
    fun toMedicalHistory(): MedicalConsultationHistory {
        val now = nowDateTime()

        val createdAtDate = createdAt?.toLocalDateFromIso() ?: now
        val updatedAtDate = updatedAt?.toLocalDateFromIso() ?: now
        val patientDOB = patientDateOfBirth?.toLocalDateFromIso() ?: now

        val examinationDateTime = examinationDate?.toLocalDateFromIso()?.plusDate(1)?.date ?: now.date

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
