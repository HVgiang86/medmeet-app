package com.huongmt.medmeet.utils.ext

import android.content.Context
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

fun Uri.readBytes(context: Context): ByteArray? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(this)
        inputStream?.use { stream ->
            val buffer = ByteArrayOutputStream()
            val data = ByteArray(1024)
            var nRead: Int
            while (stream.read(data, 0, data.size).also { nRead = it } != -1) {
                buffer.write(data, 0, nRead)
            }
            buffer.toByteArray()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Uri.getFileName(context: Context): String {
    return try {
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    it.getString(nameIndex)
                } else {
                    "avatar_${System.currentTimeMillis()}.jpg"
                }
            } else {
                "avatar_${System.currentTimeMillis()}.jpg"
            }
        } ?: "avatar_${System.currentTimeMillis()}.jpg"
    } catch (e: Exception) {
        "avatar_${System.currentTimeMillis()}.jpg"
    }
}

fun Uri.getMimeType(context: Context): String {
    return try {
        context.contentResolver.getType(this) ?: "image/jpeg"
    } catch (e: Exception) {
        "image/jpeg"
    }
} 