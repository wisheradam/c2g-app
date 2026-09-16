package co.check2go.feature.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R

enum class TravelerGender { Default, Male, Female }

/** Approved Figma My travelers states 1:28280 and 1:28289. */
@Composable
fun MyTravelersScreen(name: String?, onBack: () -> Unit, onAdd: () -> Unit, onEdit: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFF4F7FA))) {
        TravelerHeader("My travelers", onBack)
        if (name == null) {
            Column(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                Image(painterResource(R.drawable.checklists_team), null, Modifier.size(190.dp, 145.dp), contentScale = ContentScale.Fit)
                Text("Your travelers list is empty", color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "Add travelers so you don't waste any more time\non this. Add travelers to your trips, share\nchecklists with them",
                    Modifier.padding(top = 8.dp), color = Color(0xFF002349), fontSize = 16.sp,
                    lineHeight = 24.sp, textAlign = TextAlign.Center
                )
                TravelerPrimaryButton("Add a new traveler", onAdd, Modifier.padding(top = 24.dp))
            }
        } else {
            Column(Modifier.fillMaxWidth().weight(1f).padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth().height(65.dp).clip(RoundedCornerShape(13.dp)).background(Color.White)
                        .clickable(onClick = onEdit).padding(10.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(painterResource(R.drawable.traveler_avatar_generated), null, Modifier.size(44.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Fit)
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text(name, color = Color(0xFF002349), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("Traveler", color = Color(0xFF8D919A), fontSize = 16.sp)
                    }
                    Text("✎", color = Color(0xFF043CB3), fontSize = 23.sp)
                }
            }
            TravelerPrimaryButton("Add a new traveler", onAdd, Modifier.padding(horizontal = 16.dp, vertical = 20.dp))
        }
    }
}

/** Approved Figma Add traveler and Replace photo states 1:28193 and 1:28224. */
@Composable
fun TravelerEditorScreen(
    initialName: String,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by rememberSaveable(initialName) { mutableStateOf(initialName) }
    var gender by rememberSaveable { mutableStateOf(TravelerGender.Default) }
    var showPhotoSheet by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    Box(Modifier.fillMaxSize().background(Color(0xFFF4F7FA))) {
        Column(Modifier.fillMaxSize()) {
            TravelerHeader("Add a traveler", onBack)
            Column(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painterResource(R.drawable.traveler_avatar_generated), null,
                    Modifier.padding(top = 24.dp).size(44.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Fit
                )
                Text(
                    "Replace photo",
                    Modifier.padding(top = 8.dp).clickable {
                        focusManager.clearFocus(force = true)
                        keyboard?.hide()
                        showPhotoSheet = true
                    }.testTag("replace_photo"),
                    color = Color(0xFF043CB3), fontSize = 16.sp
                )
                Column(
                    Modifier.padding(top = 12.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White).padding(10.dp)
                ) {
                    Text("Full name", color = Color(0xFF002349), fontSize = 16.sp)
                    TravelerField(name, { name = it }, "Maria Bychanok", "traveler_name")
                    Text("Select gender", Modifier.padding(top = 14.dp), color = Color(0xFF002349), fontSize = 16.sp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TravelerGender.entries.forEach { option ->
                            Row(Modifier.clickable { gender = option }, verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(option == gender, { gender = option }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFC23C68)))
                                Text(option.name, color = Color(0xFF002349), fontSize = 16.sp)
                            }
                        }
                    }
                    Text("Age", color = Color(0xFF002349), fontSize = 16.sp)
                    StaticTravelerField("Child")
                    Text("Family affiliation", Modifier.padding(top = 14.dp), color = Color(0xFF002349), fontSize = 16.sp)
                    StaticTravelerField("Husband")
                }
            }
            TravelerPrimaryButton("Save changes", { onSave(name.trim()) }, Modifier.padding(horizontal = 16.dp, vertical = 20.dp), name.isNotBlank())
        }
        if (showPhotoSheet) ReplacePhotoSheet { showPhotoSheet = false }
    }
}

@Composable
private fun TravelerHeader(title: String, onBack: () -> Unit) {
    val muted = Color(0xFF8D919A)
    Row(
        Modifier.fillMaxWidth().height(98.dp).background(Color.White).statusBarsPadding().padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(R.drawable.c2g_arrow_left), "Back", Modifier.size(20.dp).clickable(onClick = onBack), colorFilter = ColorFilter.tint(muted))
        Spacer(Modifier.weight(1f)); Text(title, color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f))
        listOf(R.drawable.c2g_settings, R.drawable.c2g_help, R.drawable.c2g_notification).forEachIndexed { i, icon ->
            if (i > 0) Spacer(Modifier.width(12.dp)); Image(painterResource(icon), null, Modifier.size(21.dp), colorFilter = ColorFilter.tint(muted))
        }
    }
}

@Composable
private fun TravelerField(value: String, onChange: (String) -> Unit, placeholder: String, tag: String) {
    BasicTextField(
        value, onChange, Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(11.dp))
            .background(Color(0xFFF8FAFC)).testTag(tag).padding(horizontal = 16.dp),
        singleLine = true, textStyle = TextStyle(Color(0xFF002349), 16.sp),
        decorationBox = { inner -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) { if (value.isEmpty()) Text(placeholder, color = Color(0xFF8D919A)); inner() } }
    )
}

@Composable
private fun StaticTravelerField(value: String) {
    Row(
        Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(11.dp)).background(Color(0xFFF8FAFC)).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) { Text(value, Modifier.weight(1f), color = Color(0xFF043CB3), fontSize = 16.sp); Text("⌄", color = Color(0xFF043CB3), fontSize = 20.sp) }
}

@Composable
private fun TravelerPrimaryButton(label: String, onClick: () -> Unit, modifier: Modifier, enabled: Boolean = true) {
    Box(
        modifier.fillMaxWidth().height(45.dp).clip(RoundedCornerShape(10.dp))
            .background(if (enabled) Color(0xFF043CB3) else Color(0xFFB8C2D2)).clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text("+  $label".takeIf { label.startsWith("Add") } ?: label, color = Color.White, fontSize = 16.sp) }
}

@Composable
private fun ReplacePhotoSheet(onDismiss: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color(0x88000000))) {
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(310.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White).navigationBarsPadding().padding(18.dp)
        ) {
            Text("Replace photo", Modifier.fillMaxWidth(), color = Color(0xFF002349), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Image(painterResource(R.drawable.traveler_avatar_generated), null, Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally).size(44.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Fit)
            Spacer(Modifier.height(20.dp)); PhotoAction("▧", "Choose from gallery", Color(0xFF002349))
            Spacer(Modifier.height(20.dp)); PhotoAction("▣", "Make a photo", Color(0xFF002349))
            Spacer(Modifier.height(20.dp)); PhotoAction("♙", "Delete photo", Color(0xFFC23C68))
        }
    }
}

@Composable
private fun PhotoAction(icon: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = color, fontSize = 20.sp, modifier = Modifier.width(30.dp))
        Text(label, color = color, fontSize = 17.sp)
    }
}
