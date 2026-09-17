package co.check2go.feature.checklists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.core.design.AppDestination
import co.check2go.core.design.AppNavigationBar
import co.check2go.ui.theme.Check2GoTheme

/** Approved Figma CHECKLISTS_EMPTY state 1:28613. */
@Composable
fun ChecklistsEmptyScreen(
    onCreateChecklist: () -> Unit,
    onQuickAdd: () -> Unit,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAddLabel = stringResource(R.string.home_quick_add)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF4F7FA),
        topBar = { ChecklistsHeader() },
        bottomBar = { AppNavigationBar(AppDestination.Checklists, onDestinationSelected) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onQuickAdd, shape = CircleShape,
                containerColor = Color(0xFF043CB3), contentColor = Color.White,
                modifier = Modifier.size(56.dp).semantics { contentDescription = quickAddLabel; role = Role.Button }
            ) { Text("+", fontSize = 29.sp, fontWeight = FontWeight.Light) }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.checklists_empty_hero), null,
                Modifier.padding(top = 12.dp).size(180.dp, 145.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                stringResource(R.string.checklists_empty_hero_title),
                color = Color(0xFF002349), fontSize = 20.sp, lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.checklists_empty_supporting_copy),
                Modifier.padding(top = 6.dp), color = Color(0xFF002349),
                fontSize = 16.sp, lineHeight = 24.sp, textAlign = TextAlign.Center
            )
            CreateChecklistButton(onCreateChecklist, Modifier.padding(top = 24.dp))
            TeamChecklistsPromo(Modifier.padding(top = 39.dp))
        }
    }
}

@Composable
internal fun ChecklistsHeader() {
    val muted = Color(0xFF8D919A)
    Row(
        Modifier.fillMaxWidth().height(98.dp).background(Color.White).statusBarsPadding().padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(R.drawable.c2g_trophy), null, Modifier.size(20.dp), colorFilter = ColorFilter.tint(muted))
        Spacer(Modifier.weight(1f))
        Text("Checklists", color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.weight(1f))
        listOf(R.drawable.c2g_settings, R.drawable.c2g_help, R.drawable.c2g_notification).forEachIndexed { index, icon ->
            if (index > 0) Spacer(Modifier.width(12.dp))
            Image(painterResource(icon), null, Modifier.size(21.dp), colorFilter = ColorFilter.tint(muted))
        }
    }
}

@Composable
internal fun CreateChecklistButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().height(45.dp).clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFE4EAF2)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("+", color = Color(0xFF043CB3), fontSize = 28.sp, fontWeight = FontWeight.Light)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.checklists_create_checklist), color = Color(0xFF043CB3), fontSize = 16.sp)
        }
    }
}

@Composable
internal fun TeamChecklistsPromo(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().height(184.dp).clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF4C82B8), Color(0xFF50C9F0))))
    ) {
        Image(
            painterResource(R.drawable.checklists_team), null,
            Modifier.align(Alignment.CenterEnd).fillMaxHeight().width(180.dp), contentScale = ContentScale.Crop
        )
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Team checklists", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text(
                "You can easily share\nchecklists with other users.\nTry 7 days free trial period",
                Modifier.padding(top = 3.dp), color = Color.White, fontSize = 15.sp, lineHeight = 19.sp
            )
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
                contentAlignment = Alignment.Center
            ) { Text("Find out more", color = Color(0xFF002349), fontSize = 16.sp) }
        }
        Text("×", Modifier.align(Alignment.TopEnd).padding(12.dp), color = Color.White, fontSize = 21.sp)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ChecklistsEmptyPreview() = Check2GoTheme(false) { ChecklistsEmptyScreen({}, {}, {}) }
