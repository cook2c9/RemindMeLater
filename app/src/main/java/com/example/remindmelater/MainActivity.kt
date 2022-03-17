package com.example.remindmelater

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.remindmelater.databinding.ActivityMapsBinding
import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.service.ReminderServiceStub
import com.example.remindmelater.ui.theme.RemindMeLaterTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: View
    private var selectedReminder: Reminder? = null
    private val viewModel: MainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemindMeLaterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen("Android")
                    ReminderListItem()
                    Map()
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        runBlocking { addSavedReminders() }
        moveMapCamera(39.103,-84.512)
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
                        Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show()
                        hideMap()
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
                        showMap()
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
    fun ReminderListItem() {
        var reminderData = viewModel.fetchReminders()

        Log.d(TAG, "Results Array: $reminderData")
        Column() {
            Text(text = "Reminder: ${reminderData}")
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

    @Composable
    private fun Map() {
        val binding = ActivityMapsBinding.inflate(layoutInflater)
        addContentView(binding.root, ViewGroup.LayoutParams(-1, -1))

//     Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)
    }

    private fun hideMap() {
        mapView = findViewById(R.id.map)
        mapView.visibility = View.INVISIBLE
    }

    private fun showMap() {
        mapView = findViewById(R.id.map)
        mapView.visibility = View.VISIBLE
    }

    private fun addMapMarker(label: String, lat: Double, long: Double) {
        // Adds a map marker with a label at the given lat and long.
        val loc = LatLng(lat,long)
        mMap.addMarker(MarkerOptions().position(loc).title(label))
    }

    private fun moveMapCamera(lat: Double, long: Double) {
        // Moves camera location to given lat and long
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat,long)))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5f))
    }

    private suspend fun addSavedReminders() {
        val savedReminders: List<Reminder>? = ReminderServiceStub().fetchReminders()
        savedReminders?.let {
            it.forEach{ reminder ->
                addMapMarker(reminder.title, reminder.latitude, reminder.longitude)
            }
        }
    }
}