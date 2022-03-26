package com.example.remindmelater

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmelater.ReminderRecyclerView.ReminderAdapter
import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.ui.theme.RemindMeLaterTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mMap: GoogleMap
    private var selectedReminder: Reminder? = null
    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var reminderArrayList: ArrayList<Reminder>
    private lateinit var reminderAdapter: ReminderAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        reminderArrayList = arrayListOf()

        reminderAdapter = ReminderAdapter(reminderArrayList)

        recyclerView.adapter = reminderAdapter

        setContent {
            RemindMeLaterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                    ReminderRow()
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        RemindMeLaterTheme {
            MainScreen()
            ReminderRow()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        Column {
            TopAppBar(
                elevation = 4.dp,
                title = {
                    Text("Remind Me Later")
                },
                backgroundColor = Color(105, 208, 225),
                navigationIcon = {
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Menu, null)
                    }
                }, actions = {
                    IconButton(onClick = { /*showDialog.value = true*/ }) {
                        Icon(Icons.Filled.Settings, null)
                    }
                })

            Text(
                text = "Hello, Set a Reminder for...",
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show()
                        Log.d("MESSAGE: ", "Myself Button Clicked")
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(190.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(12, 121, 230))
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = "Myself")
                }
                Button(
                    onClick = {
                        Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show()
                        Log.d("MESSAGE: ", "Others Button Clicked")
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(190.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(12, 121, 230))
                ) {
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
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Log.d("MESSAGE: ", "Reminder List Button Clicked")
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
                        Log.d("MESSAGE: ", "Map View Button Clicked")
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
                Column() {

                }
            }

        }

    }

    @Composable
    fun ReminderRow(){
       Log.d("Array List", " ")

        val reminders_ = remember { mutableStateListOf(Reminder())}

        viewModel.fetchReminders(reminders_)

        Row (
            modifier = Modifier.padding(vertical = 200.dp)
        ){
            LazyColumn() {
                items(reminders_) { item: Reminder ->
                    ReminderListItem(item)
                }
            }
        }
    }

    @Composable
    fun ReminderListItem(reminder: Reminder) {
        Log.d("Reminder List ", "Loaded Successfully")
        Column(

        ) {
            Text(text = "Reminder: ${reminder.body}")
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp)
            ) {
                Text(text = "Location: ")
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp, top = 0.dp),
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp, top = 0.dp)
                )
                Text(text = "For: ${reminder.userEmail}")
            }
        }
    }
}