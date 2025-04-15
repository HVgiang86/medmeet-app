package com.gianghv.kmachat.theme.icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Exclamation: ImageVector
    get() {
        if (_Exclamation != null) {
            return _Exclamation!!
        }
        _Exclamation =
            ImageVector
                .Builder(
                    name = "Exclamation",
                    defaultWidth = 16.dp,
                    defaultHeight = 16.dp,
                    viewportWidth = 16f,
                    viewportHeight = 16f,
                ).apply {
                    path(
                        fill = SolidColor(Color(0xFF000000)),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(7.002f, 11f)
                        arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, 2f, 0f)
                        arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 0f)
                        moveTo(7.1f, 4.995f)
                        arcToRelative(0.905f, 0.905f, 0f, isMoreThanHalf = true, isPositiveArc = true, 1.8f, 0f)
                        lineToRelative(-0.35f, 3.507f)
                        arcToRelative(0.553f, 0.553f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.1f, 0f)
                        close()
                    }
                }.build()
        return _Exclamation!!
    }

private var _Exclamation: ImageVector? = null
