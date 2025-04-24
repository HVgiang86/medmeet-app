package com.huongmt.medmeet.ui.home.list

enum class HomeItemType {
    HEADER, CLINIC_ITEM, NO_CLINICS, PADDING_BOTTOM
}

fun getItemList(clinicsAmount: Int = 0): MutableList<HomeItemType> {
    return mutableListOf(
        HomeItemType.HEADER
    ).apply {
        if (clinicsAmount == 0) {
            add(HomeItemType.NO_CLINICS)
        } else {
            repeat(clinicsAmount) {
                add(HomeItemType.CLINIC_ITEM)
            }
        }
    }.apply {
        add(HomeItemType.PADDING_BOTTOM)
    }
}

fun getClinicItemIndex(itemIndex: Int, listSize: Int): Int {
    return (itemIndex - 1).coerceIn(0, listSize - 1)
}
