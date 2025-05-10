package com.huongmt.medmeet.shared.app

import com.huongmt.medmeet.shared.base.Store
import com.huongmt.medmeet.shared.core.WholeApp
import com.huongmt.medmeet.shared.core.entity.BloodType
import com.huongmt.medmeet.shared.core.entity.HealthRecord
import com.huongmt.medmeet.shared.core.repository.HealthRecordRepository

data class HealthRecordState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val healthRecord: HealthRecord? = null,
    val bmi: Double? = 0.0,
    val isEditMode: Boolean = false
) : Store.State(isLoading)

sealed interface HealthRecordAction : Store.Action {
    data object GetHealthRecord : HealthRecordAction
    data class GetHealthRecordSuccess(val healthRecord: HealthRecord) : HealthRecordAction
    data object DismissError : HealthRecordAction
    data class Error(val error: Throwable) : HealthRecordAction
    data object ToggleEditMode : HealthRecordAction
    data class UpdateHealthRecord(
        val bloodType: BloodType?,
        val height: Int?,
        val weight: Int?,
        val healthHistory: String?
    ) : HealthRecordAction

    data class UpdateHealthRecordSuccess(val healthRecord: HealthRecord) : HealthRecordAction
}

sealed class HealthRecordEffect : Store.Effect {
    data object ShowEditBottomSheet : HealthRecordEffect()
}

class HealthRecordStore(
    private val healthRecordRepository: HealthRecordRepository
) : Store<HealthRecordState, HealthRecordAction, HealthRecordEffect>(
    initialState = HealthRecordState()
) {
    override val onException: (Throwable) -> Unit
        get() = {
            sendAction(HealthRecordAction.Error(it))
        }

    override fun dispatch(oldState: HealthRecordState, action: HealthRecordAction) {
        when (action) {
            is HealthRecordAction.Error -> {
                setState(oldState.copy(isLoading = false, error = action.error))
            }

            HealthRecordAction.GetHealthRecord -> {
                setState(oldState.copy(isLoading = true))
                getHealthRecord()
            }

            is HealthRecordAction.GetHealthRecordSuccess -> {
                val bmi = calculateBMI(
                    action.healthRecord.height,
                    action.healthRecord.weight
                )
                setState(
                    oldState.copy(
                        healthRecord = action.healthRecord,
                        bmi = bmi,
                        isLoading = false,
                        error = null
                    )
                )
            }

            HealthRecordAction.DismissError -> {
                setState(oldState.copy(error = null))
            }

            HealthRecordAction.ToggleEditMode -> {
                setState(oldState.copy(isEditMode = !oldState.isEditMode))
                if (!oldState.isEditMode) {
                    setEffect(HealthRecordEffect.ShowEditBottomSheet)
                }
            }

            is HealthRecordAction.UpdateHealthRecord -> {
                setState(oldState.copy(isLoading = true, isEditMode = false))
                println("Update health record: ${action.bloodType}, ${action.height}, ${action.weight}, ${action.healthHistory}")
                updateHealthRecord(
                    action.bloodType,
                    action.height,
                    action.weight,
                    action.healthHistory
                )
            }

            is HealthRecordAction.UpdateHealthRecordSuccess -> {
                val bmi = calculateBMI(
                    action.healthRecord.height,
                    action.healthRecord.weight
                )
                setState(
                    oldState.copy(
                        healthRecord = action.healthRecord,
                        isLoading = false,
                        error = null,
                        isEditMode = false,
                        bmi = bmi
                    )
                )
            }
        }
    }

    private fun getHealthRecord() {
        runFlow(exception = coroutineExceptionHandler { }) {
            val userId = WholeApp.USER?.id ?: ""
            healthRecordRepository.getHealthRecord(userId).collect {
                sendAction(HealthRecordAction.GetHealthRecordSuccess(it))
            }
        }
    }

    private fun calculateBMI(
        height: Int?,
        weight: Int?
    ): Double {
        if (height == null || weight == null) return 0.0
        val heightInMeters = height / 100.0
        return weight / (heightInMeters * heightInMeters)
    }

    private fun updateHealthRecord(
        bloodType: BloodType?,
        height: Int?,
        weight: Int?,
        healthHistory: String?
    ) {
        runFlow {
            val userId = WholeApp.USER?.id ?: ""
            healthRecordRepository.updateHealthRecord(
                userId = userId,
                bloodType = bloodType,
                height = height,
                weight = weight,
                healthHistory = healthHistory
            ).collect {
                sendAction(HealthRecordAction.UpdateHealthRecordSuccess(it))
            }
        }
    }
}
