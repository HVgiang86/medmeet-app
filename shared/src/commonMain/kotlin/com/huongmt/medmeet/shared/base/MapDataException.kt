package com.huongmt.medmeet.shared.base

class MapDataException(
    message: String = "Map data error",
    cause: Throwable? = null
) : Exception(message, cause)
