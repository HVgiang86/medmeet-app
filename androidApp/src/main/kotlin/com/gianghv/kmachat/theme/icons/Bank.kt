package com.gianghv.kmachat.theme.icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val IC_BANK: ImageVector
    get() {
        if (_Account_balance != null) {
            return _Account_balance!!
        }
        _Account_balance =
            ImageVector
                .Builder(
                    name = "Account_balance",
                    defaultWidth = 24.dp,
                    defaultHeight = 24.dp,
                    viewportWidth = 960f,
                    viewportHeight = 960f,
                ).apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1.0f,
                        stroke = null,
                        strokeAlpha = 1.0f,
                        strokeLineWidth = 1.0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 1.0f,
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(200f, 680f)
                        verticalLineToRelative(-280f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(280f)
                        close()
                        moveToRelative(240f, 0f)
                        verticalLineToRelative(-280f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(280f)
                        close()
                        moveTo(80f, 840f)
                        verticalLineToRelative(-80f)
                        horizontalLineToRelative(800f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(600f, -160f)
                        verticalLineToRelative(-280f)
                        horizontalLineToRelative(80f)
                        verticalLineToRelative(280f)
                        close()
                        moveTo(80f, 320f)
                        verticalLineToRelative(-80f)
                        lineToRelative(400f, -200f)
                        lineToRelative(400f, 200f)
                        verticalLineToRelative(80f)
                        close()
                        moveToRelative(178f, -80f)
                        horizontalLineToRelative(444f)
                        close()
                        moveToRelative(0f, 0f)
                        horizontalLineToRelative(444f)
                        lineTo(480f, 130f)
                        close()
                    }
                }.build()
        return _Account_balance!!
    }

private var _Account_balance: ImageVector? = null
