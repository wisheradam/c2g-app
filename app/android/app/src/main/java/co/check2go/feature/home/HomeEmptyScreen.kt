package co.check2go.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.core.design.AppDestination
import co.check2go.core.design.AppHeader
import co.check2go.core.design.AppNavigationBar
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma HOME_EMPTY frame 1:28510. */
@Composable
fun HomeEmptyScreen(
    onAddTrip: () -> Unit,
    onQuickAdd: () -> Unit,
    onDestinationSelected: (AppDestination) -> Unit,
    onAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val quickAddLabel = stringResource(R.string.home_quick_add)

    Box(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            AppHeader(onAccountClick)

            Box(
                Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp).fillMaxWidth().height(183.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF4F88BC), Color(0xFF58C5EC))))
            ) {
                Box(Modifier.align(Alignment.TopEnd).offset(x = 57.dp, y = 20.dp).size(222.dp)
                    .background(Color(0xFFF4F7FA).copy(alpha = .28f), CircleShape))
                Image(
                    painterResource(R.drawable.home_suitcase), null, contentScale = ContentScale.Fit,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 22.dp, y = 31.dp).size(178.dp)
                )
                Image(
                    painterResource(R.drawable.home_hat), null, contentScale = ContentScale.Fit,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 7.dp, y = 26.dp).size(102.dp)
                )
                Text(
                    stringResource(R.string.home_hero_title), Modifier.padding(start = 16.dp, top = 16.dp).width(207.dp),
                    color = Color.White, fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold
                )
                Text(
                    stringResource(R.string.home_supporting_copy), Modifier.padding(start = 16.dp, top = 74.dp).width(207.dp),
                    color = Color.White, fontSize = 14.sp, lineHeight = 18.sp
                )
                Box(
                    Modifier.align(Alignment.BottomCenter).padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth().height(42.dp).clip(RoundedCornerShape(12.dp)).background(Color.White)
                        .clickable(role = Role.Button, onClick = onAddTrip),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.home_add_trip), color = Color(0xFF002349), fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.weight(1f))
            AppNavigationBar(AppDestination.Home, onDestinationSelected)
        }

        Box(
            Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(end = 8.dp, bottom = 74.dp)
                .size(56.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                .semantics { contentDescription = quickAddLabel; role = Role.Button }
                .clickable(onClick = onQuickAdd),
            contentAlignment = Alignment.Center
        ) { Text("+", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Light) }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun HomeEmptyLightPreview() {
    Check2GoTheme(darkTheme = false) { HomeEmptyScreen({}, {}, {}) }
}
