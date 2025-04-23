package com.huongmt.medmeet.theme.icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Shield: ImageVector
    get() {
        if (_Shield != null) {
            return _Shield!!
        }
        _Shield =
            ImageVector
                .Builder(
                    name = "Shield",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 24f,
                    viewportHeight = 24f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                        fillAlpha = 1.0f,
                        stroke = SolidColor(Color(0xFF000000)),
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 2f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(20f, 13f)
                        curveToRelative(0f, 5f, -3.5f, 7.5f, -7.66f, 8.95f)
                        arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.67f, -0.01f)
                        curveTo(7.5f, 20.5f, 4f, 18f, 4f, 13f)
                        verticalLineTo(6f)
                        arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, -1f)
                        curveToRelative(2f, 0f, 4.5f, -1.2f, 6.24f, -2.72f)
                        arcToRelative(1.17f, 1.17f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.52f, 0f)
                        curveTo(14.51f, 3.81f, 17f, 5f, 19f, 5f)
                        arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 1f)
                        close()
                    }
                }.build()
        return _Shield!!
    }

private var _Shield: ImageVector? = null
