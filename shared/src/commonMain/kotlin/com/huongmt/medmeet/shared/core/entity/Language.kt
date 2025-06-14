package com.huongmt.medmeet.shared.core.entity

enum class Language(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    ENGLISH("en", "English", "English"),
    VIETNAMESE("vi", "Vietnamese", "Tiếng Việt");

    companion object {
        fun fromCode(code: String): Language {
            return values().find { it.code == code } ?: ENGLISH
        }

        fun getDefault(): Language = ENGLISH
    }
} 