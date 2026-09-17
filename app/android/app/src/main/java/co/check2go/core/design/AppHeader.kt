package co.check2go.core.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme
import co.check2go.R

@Composable
internal fun AppHeader(
    onAccountClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onToggleTheme: (() -> Unit)? = null,
    isDarkTheme: Boolean = false
) {
    val iconColor = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier.fillMaxWidth().height(98.dp).background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIcon(R.drawable.c2g_trophy, iconColor, stringResource(R.string.personal_account), onAccountClick)
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.app_name),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        HeaderIcon(R.drawable.c2g_settings, iconColor, stringResource(R.string.settings_title), onSettingsClick)
        Spacer(Modifier.width(10.dp))
        if (onToggleTheme != null) {
            Text(
                text = if (isDarkTheme) "☀" else "☾",
                color = iconColor,
                fontSize = 24.sp,
                modifier = Modifier.size(24.dp).clickable(
                    onClickLabel = stringResource(if (isDarkTheme) R.string.theme_switch_to_light else R.string.theme_switch_to_dark),
                    onClick = onToggleTheme
                )
            )
            Spacer(Modifier.width(10.dp))
        }
        HeaderIcon(R.drawable.c2g_help, iconColor)
        Spacer(Modifier.width(10.dp))
        Box {
            HeaderIcon(R.drawable.c2g_notification, iconColor)
            Box(
                Modifier.align(Alignment.TopEnd).size(5.dp)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
            )
        }
    }
}

@Composable
private fun HeaderIcon(
    resourceId: Int,
    color: Color,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null
) {
    Image(
        painter = painterResource(resourceId),
        contentDescription = contentDescription,
        colorFilter = ColorFilter.tint(color),
        modifier = Modifier.size(24.dp).then(
            if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)
        )
    )
}
