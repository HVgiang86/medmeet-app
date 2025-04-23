package com.huongmt.medmeet.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.app_name),
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null,
    isTop: Boolean = false,
) {
    Surface(
        modifier = modifier.wrapContentSize(),
        border = if (!isTop) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Back",
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onOpenDrawer()
                        },
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_new_chat),
                contentDescription = "New chat",
                modifier =
                    Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                        },
            )
        }
    }
}
