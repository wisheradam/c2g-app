package co.check2go.feature.trip

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.check2go.R
import co.check2go.ui.theme.Check2GoTheme

data class TripDestinationDraft(
    val destinationCountry: String,
    val departureCountry: String,
    val tripName: String
)

/** Approved Figma destination states 1:29302, 1:29314 and 1:29326. */
@Composable
fun TripCreateDestinationScreen(
    destinationCountry: String,
    onDestinationCountryChange: (String) -> Unit,
    departureCountry: String,
    onDepartureCountryChange: (String) -> Unit,
    tripName: String,
    onTripNameChange: (String) -> Unit,
    onAutofill: () -> Unit,
    onBack: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize().background(Color.White)) {
        DestinationHeader(onBack)
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Image(
                painterResource(R.drawable.trip_destination_malaga), null,
                Modifier.fillMaxSize(), contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.55f)), startY = 250f)))
            Column(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    stringResource(R.string.trip_create_destination_title),
                    color = Color.White, fontSize = 32.sp, lineHeight = 38.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                DestinationField(
                    destinationCountry,
                    onDestinationCountryChange,
                    stringResource(R.string.trip_autofill),
                    stringResource(R.string.trip_destination_country),
                    ImeAction.Next,
                    onAutofill
                )
                DestinationField(
                    departureCountry,
                    onDepartureCountryChange,
                    stringResource(R.string.trip_departure_country),
                    stringResource(R.string.trip_departure_country),
                    ImeAction.Next
                )
                DestinationField(
                    tripName,
                    onTripNameChange,
                    stringResource(R.string.trip_name),
                    stringResource(R.string.trip_name),
                    ImeAction.Done
                )
                Box(
                    Modifier.fillMaxWidth().height(51.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF043CB3)).clickable(onClick = onStart),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.trip_start), color = Color.White, fontSize = 18.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun DestinationHeader(onBack: () -> Unit) {
    val secondary = Color(0xFF8D919A)
    val backLabel = stringResource(R.string.trip_back)
    Row(
        Modifier.fillMaxWidth().height(98.dp).background(Color.White).statusBarsPadding().padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.c2g_arrow_left),
            contentDescription = null,
            colorFilter = ColorFilter.tint(secondary),
            modifier = Modifier.size(24.dp).clickable(onClick = onBack).semantics {
                text = AnnotatedString(backLabel)
            }
        )
        Spacer(Modifier.weight(1f)); Text("Add a trip", color = Color(0xFF002349), fontSize = 18.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f))
        DestinationHeaderIcon(R.drawable.c2g_settings, secondary); Spacer(Modifier.width(10.dp)); DestinationHeaderIcon(R.drawable.c2g_help, secondary); Spacer(Modifier.width(10.dp)); DestinationHeaderIcon(R.drawable.c2g_notification, secondary)
    }
}

@Composable
private fun DestinationHeaderIcon(id: Int, tint: Color) {
    Image(painterResource(id), null, colorFilter = ColorFilter.tint(tint), modifier = Modifier.size(24.dp))
}

@Composable
private fun DestinationField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    semanticLabel: String,
    imeAction: ImeAction,
    onPlaceholderAction: (() -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().height(49.dp).clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF4F7FA).copy(alpha = .92f)).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f).semantics { text = AnnotatedString(semanticLabel) },
            singleLine = true,
            textStyle = TextStyle(color = Color(0xFF002349), fontSize = 16.sp, lineHeight = 24.sp),
            keyboardOptions = KeyboardOptions(
                capitalization = if (imeAction == ImeAction.Done) KeyboardCapitalization.Sentences else KeyboardCapitalization.Words,
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            ),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(
                    placeholder,
                    Modifier
                        .clickable(enabled = onPlaceholderAction != null) { onPlaceholderAction?.invoke() }
                        .clearAndSetSemantics { },
                    color = if (onPlaceholderAction != null) Color(0xFF043CB3) else Color(0xFF8D919A),
                    fontSize = 16.sp
                )
                else inner()
            }
        )
        Text("×", Modifier.clickable { onValueChange("") }, color = Color(0xFF043CB3), fontSize = 22.sp)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun DestinationPreview() = Check2GoTheme(false) { TripCreateDestinationScreen("", {}, "", {}, "", {}, {}, {}, {}) }
