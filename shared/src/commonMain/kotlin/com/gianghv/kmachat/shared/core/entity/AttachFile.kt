package com.gianghv.kmachat.shared.core.entity

data class AttachFile(
    val id: String,
    val name: String,
    val size: Long,
    val type: String,
    val url: String
)
