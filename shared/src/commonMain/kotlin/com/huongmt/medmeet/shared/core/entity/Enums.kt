package com.huongmt.medmeet.shared.core.entity

enum class Gender(val value: Int, val text: String) {
    MALE(1, "Nam"),
    FEMALE(2, "Nữ"),
    OTHER(3, "Khác");
}

enum class ActiveStatus(val value: Int, val text: String) {
    ACTIVE(1, "Hoạt động"),
    INACTIVE(2, "Dừng hoạt động");
}

enum class MedicalServiceType(val value: Int, val text: String) {
    SPECIALITY(1, "Chuyên khoa"),
    EXAMINATION_PACKAGE(2, "Gói khám");
}

enum class PaymentMethod(val value: Int, val text: String) {
    CASH(1, "Tiền mặt"),
    VNPAY(2, "VNPAY");
}

enum class PaymentStatus(val value: Int, val text: String) {
    SUCCESS(2, "Đã thanh toán"),
    FAILED(3, "Chưa thanh toán");
}

enum class MedicalRecordStatus(val value: Int, val text: String) {
    PENDING(1, "Chưa khám"),
    CANCELED(2, "Đã hủy"),
    COMPLETED(3, "Đã hoàn thành");
}

enum class BloodType(val text: String, val value: String) {
    A_POS("Nhóm máu A+", "a+"),
    A_NEG("Nhóm máu A-", "a-"),
    B_POS("Nhóm máu B+", "b+"),
    B_NEG("Nhóm máu B-", "b-"),
    AB_POS("Nhóm máu AB+", "ab+"),
    AB_NEG("Nhóm máu AB-", "ab-"),
    O_POS("Nhóm máu O+", "o+"),
    O_NEG("Nhóm máu O-", "o-"),
    NA("Chưa xác định", "NA");
}

enum class NotificationType(val route: String) {
    USER("user"),
    HEALTH_RECORD("health_record"),
    MEDICAL_CONSULTATION_HISTORY("medical-consultation-history"),
    OTHER("other")
}

enum class NotificationAction(val action: String) {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE")
}

enum class NotificationDetailRoute {
    GoToUserProfile,
    GoToHealthRecord,
    GoToBookingDetail,
    NoAction
}

fun getNotificationDetailRoute(type: NotificationType? = null, action: NotificationAction? = null): NotificationDetailRoute {
    return when (type) {
        NotificationType.USER -> {
            when (action) {
                NotificationAction.CREATE -> NotificationDetailRoute.GoToUserProfile
                NotificationAction.UPDATE -> NotificationDetailRoute.GoToUserProfile
                NotificationAction.DELETE -> NotificationDetailRoute.NoAction
                null -> NotificationDetailRoute.NoAction
            }
        }
        NotificationType.HEALTH_RECORD -> {
            when (action) {
                NotificationAction.CREATE -> NotificationDetailRoute.GoToHealthRecord
                NotificationAction.UPDATE -> NotificationDetailRoute.GoToHealthRecord
                NotificationAction.DELETE -> NotificationDetailRoute.NoAction
                null -> NotificationDetailRoute.NoAction
            }
        }
        NotificationType.MEDICAL_CONSULTATION_HISTORY -> {
            when (action) {
                NotificationAction.CREATE -> NotificationDetailRoute.GoToBookingDetail
                NotificationAction.UPDATE -> NotificationDetailRoute.GoToBookingDetail
                NotificationAction.DELETE -> NotificationDetailRoute.NoAction
                null -> NotificationDetailRoute.NoAction
            }
        }

        NotificationType.OTHER -> NotificationDetailRoute.NoAction
        null -> NotificationDetailRoute.NoAction
    }
}
