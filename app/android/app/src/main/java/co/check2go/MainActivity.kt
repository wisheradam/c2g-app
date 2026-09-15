package co.check2go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.check2go.app.Check2GoApp
import co.check2go.ui.theme.Check2GoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Check2GoTheme {
                Check2GoApp(onTripStart = {})
            }
        }
    }
}
