package com.example.remindmelater

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
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
                    MainScreen("Android")
                }
            }
        }
    }
}

@Composable
fun MainScreen(name: String) {
    val context = LocalContext.current
    Column {
        TopAppBar(
            elevation = 4.dp,
            title = {
                Text("Remind Me Later")
            },
            backgroundColor = Color(105,208,225),
            navigationIcon = {
                IconButton(onClick = {/* Do Something*/ }) {
                    Icon(Icons.Filled.Menu, null)
                }
            }, actions = {
                IconButton(onClick = { /*showDialog.value = true*/ }) {
                    Icon(Icons.Filled.Settings, null)
                }
            })

        Text(text = "Hello, Set a Reminder for...",
             modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp))
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
                ) {
            Button(onClick = { Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show() },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(190.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(12,121,230))
                ){
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = "Myself")
            }
            Button(onClick = { Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show() },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(190.dp)
                        .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(12,121,230))
                ){
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                )
                Text(text = "Others")
            }
        }
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .padding(4.dp)
                    .height(35.dp)
                    .width(190.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(12, 121, 230))
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = "Reminder List")
            }
            Button(
                onClick = {
                    Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .padding(4.dp)
                    .height(35.dp)
                    .width(190.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(12, 121, 230))
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = "Map View")
            }
        }
        Scaffold { innerPadding ->
            LazyColumn(contentPadding = innerPadding){

            }
        }

    }

@Composable
fun ReminderListItem() {
   Column() {
       Text(text = "Reminder:")
       Row(
           horizontalArrangement = Arrangement.SpaceEvenly,
           verticalAlignment = Alignment.CenterVertically,
           modifier = Modifier
               .fillMaxWidth()
               .padding(7.dp)
       ) {
            Text(text = "Location:")
           Icon(
               imageVector = Icons.Default.Edit,
               contentDescription = null,
               modifier = Modifier.padding(end = 4.dp)
           )
           Icon(
               imageVector = Icons.Default.Delete,
               contentDescription = null,
               modifier = Modifier.padding(end = 4.dp)
           )
           Text(text = "For:")
       }
   }

}

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RemindMeLaterTheme {
        MainScreen("Android")
    }
}



