package com.example.remindmelater

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.remindmelater.ui.theme.RemindMeLaterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemindMeLaterTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val context = LocalContext.current
    Column {
        Text(text = "Hello, Set a Reminder for...")
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly
                ) {
            Button(onClick = { Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show() },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(175.dp)
                        .height(50.dp)
                ){
                Text(text = "Myself")
            }
            Button(onClick = { Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show() },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(174.dp)
                        .height(50.dp)
                ){
                Text(text = "Others")
            }
        }
        Button(onClick = { Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .height(35.dp)
            ){
            Text(text = "View Reminder List")
        }

    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RemindMeLaterTheme {
        Greeting("Android")
    }
}