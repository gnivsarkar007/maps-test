package com.example.mapstest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mapstest.nativemap.MapsActivity
import com.example.mapstest.svg.SVGMapActivity
import com.example.mapstest.ui.theme.MapsTestTheme

class EntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapsTestTheme {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(2) {
                            val target = when(it) {
                                0 -> "Native Map" to MapsActivity::class.java
                                else -> "SVG Map" to SVGMapActivity:: class.java
                            }

                            Greeting(name = target.first, modifier = Modifier.fillMaxWidth().height(32.dp).background(color = androidx.compose.ui.graphics.Color.Gray).clickable {
                                startActivity(Intent(this@EntryActivity, target.second))
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MapsTestTheme {
        Greeting("Android")
    }
}