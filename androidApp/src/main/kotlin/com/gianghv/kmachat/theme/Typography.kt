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
fun textStyle(
    fontSize: Int,
    fontWeight: FontWeight,
    fontFamily: FontFamily? = null,
): TextStyle {
    return TextStyle(
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        fontFamily = fontFamily ?: Fonts.interFontFamily,
        lineHeight = (fontSize * 1.5).sp,
    )
}

// Define the Typography object

val Typography
    @Composable get() = Typography(
        displayLarge = textStyle(57, FontWeight.Normal),
        displayMedium = textStyle(45, FontWeight.Normal),
        displaySmall = textStyle(36, FontWeight.Normal),

        headlineLarge = textStyle(32, FontWeight.Normal),
        headlineMedium = textStyle(28, FontWeight.Normal),
        headlineSmall = textStyle(24, FontWeight.Normal),

        titleLarge = textStyle(22, FontWeight.Medium),
        titleMedium = textStyle(16, FontWeight.Medium),
        titleSmall = textStyle(14, FontWeight.Medium),

        bodyLarge = textStyle(16, FontWeight.Normal),
        bodyMedium = textStyle(14, FontWeight.Normal),
        bodySmall = textStyle(12, FontWeight.Normal),

        labelLarge = textStyle(14, FontWeight.Medium),
        labelMedium = textStyle(12, FontWeight.Medium),
        labelSmall = textStyle(11, FontWeight.Medium)
    )
