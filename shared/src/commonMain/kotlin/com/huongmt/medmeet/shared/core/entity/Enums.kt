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

object VietnamProvinces {
    val provinces = listOf(
        "An Giang",
        "Bà Rịa - Vũng Tàu",
        "Bắc Giang",
        "Bắc Kạn",
        "Bạc Liêu",
        "Bắc Ninh",
        "Bến Tre",
        "Bình Định",
        "Bình Dương",
        "Bình Phước",
        "Bình Thuận",
        "Cà Mau",
        "Cần Thơ",
        "Cao Bằng",
        "Đà Nẵng",
        "Đắk Lắk",
        "Đắk Nông",
        "Điện Biên",
        "Đồng Nai",
        "Đồng Tháp",
        "Gia Lai",
        "Hà Giang",
        "Hà Nam",
        "Hà Nội",
        "Hà Tĩnh",
        "Hải Dương",
        "Hải Phòng",
        "Hậu Giang",
        "Hòa Bình",
        "Hưng Yên",
        "Khánh Hòa",
        "Kiên Giang",
        "Kon Tum",
        "Lai Châu",
        "Lâm Đồng",
        "Lạng Sơn",
        "Lào Cai",
        "Long An",
        "Nam Định",
        "Nghệ An",
        "Ninh Bình",
        "Ninh Thuận",
        "Phú Thọ",
        "Phú Yên",
        "Quảng Bình",
        "Quảng Nam",
        "Quảng Ngãi",
        "Quảng Ninh",
        "Quảng Trị",
        "Sóc Trăng",
        "Sơn La",
        "Tây Ninh",
        "Thái Bình",
        "Thái Nguyên",
        "Thanh Hóa",
        "Thừa Thiên Huế",
        "Tiền Giang",
        "TP. Hồ Chí Minh",
        "Trà Vinh",
        "Tuyên Quang",
        "Vĩnh Long",
        "Vĩnh Phúc",
        "Yên Bái"
    )
}
