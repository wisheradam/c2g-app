package co.check2go.feature.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsScreen(account: PersonalAccount, onAccountChange: (PersonalAccount) -> Unit, onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    var value by remember { mutableStateOf("") }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Interests") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } })
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value, { value = it }, label = { Text("New interest") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        val label = value.trim()
                        if (label.isNotEmpty()) {
                            val id = label.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').ifBlank { "interest-${System.nanoTime()}" }
                            onAccountChange(account.withInterestAdded(Interest(id, label)))
                            value = ""
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("+ Add interest") }
                }
            }
            if (account.interests.isEmpty()) item { Text("No interests added") }
            items(account.interests, key = { it.id }) { interest ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(interest.label)
                    TextButton(onClick = { onAccountChange(account.withInterestRemoved(interest.id)) }) { Text("Remove") }
                }
            }
        }
    }
}
