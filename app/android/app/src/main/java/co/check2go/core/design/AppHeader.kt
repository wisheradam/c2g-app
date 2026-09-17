package co.check2go.core.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import co.check2go.R

@Composable
internal fun AppHeader() {
    val iconColor = Color(0xFF8D919A)
    Row(
        modifier = Modifier.fillMaxWidth().height(98.dp).background(Color.White)
            .statusBarsPadding().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderIcon(R.drawable.c2g_trophy, iconColor)
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.app_name),
            color = Color(0xFF002349),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        HeaderIcon(R.drawable.c2g_settings, iconColor)
        Spacer(Modifier.width(10.dp))
        HeaderIcon(R.drawable.c2g_help, iconColor)
        Spacer(Modifier.width(10.dp))
        Box {
            HeaderIcon(R.drawable.c2g_notification, iconColor)
            Box(
                Modifier.align(Alignment.TopEnd).size(5.dp)
                    .background(Color(0xFFC45777), CircleShape)
            )
        }
    }
}

@Composable
private fun HeaderIcon(resourceId: Int, color: Color) {
    Image(
        painter = painterResource(resourceId),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color),
        modifier = Modifier.size(24.dp)
    )
}
