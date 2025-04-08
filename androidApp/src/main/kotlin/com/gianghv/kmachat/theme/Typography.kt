package com.gianghv.kmachat.theme


import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gianghv.kmachat.R

object Fonts {
    val interFontFamily
        @Composable get() = FontFamily(
            Font(R.font.inter_regular, FontWeight.Normal, FontStyle.Normal),
            Font(R.font.inter_medium, FontWeight.Medium, FontStyle.Normal),
            Font(R.font.inter_semibold, FontWeight.SemiBold, FontStyle.Normal),
            Font(R.font.inter_bold, FontWeight.Bold, FontStyle.Normal),
            Font(R.font.inter_thin, FontWeight.Thin, FontStyle.Normal),
            Font(R.font.inter_light, FontWeight.Light, FontStyle.Normal),
        )

    val popinsFontFamily
        @Composable get() = FontFamily(
            Font(R.font.poppins_regular, FontWeight.Normal, FontStyle.Normal),
            Font(R.font.poppins_medium, FontWeight.Medium, FontStyle.Normal),
            Font(R.font.poppins_semibold, FontWeight.SemiBold, FontStyle.Normal),
            Font(R.font.poppins_bold, FontWeight.Bold, FontStyle.Normal)
        )
}

// Define a reusable function to create a TextStyle with optional fontFamily
@Composable
fun medmeetTextStyle(
    fontSize: Int,
    fontWeight: FontWeight,
    fontFamily: FontFamily? = null,
): TextStyle {
    return TextStyle(
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        fontFamily = fontFamily ?: Fonts.interFontFamily // Default to Inter
    )
}

val Typography
    @Composable get() = Typography(
        displayLarge = medmeetTextStyle(57, FontWeight.Normal),
        displayMedium = medmeetTextStyle(45, FontWeight.Normal),
        displaySmall = medmeetTextStyle(36, FontWeight.Normal),
        headlineLarge = medmeetTextStyle(32, FontWeight.Normal),
        headlineMedium = medmeetTextStyle(28, FontWeight.Normal),
        headlineSmall = medmeetTextStyle(24, FontWeight.Normal),
        titleLarge = medmeetTextStyle(20, FontWeight.SemiBold),
        titleMedium = medmeetTextStyle(16, FontWeight.SemiBold),
        titleSmall = medmeetTextStyle(14, FontWeight.SemiBold),
        bodyLarge = medmeetTextStyle(16, FontWeight.SemiBold),
        bodyMedium = medmeetTextStyle(14, FontWeight.Normal),
        bodySmall = medmeetTextStyle(12, FontWeight.Normal),
        labelLarge = medmeetTextStyle(14, FontWeight.SemiBold),
        labelMedium = medmeetTextStyle(12, FontWeight.SemiBold),
        labelSmall = medmeetTextStyle(11, FontWeight.SemiBold)
    )
