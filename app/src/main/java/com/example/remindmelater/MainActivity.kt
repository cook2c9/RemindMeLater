package com.example.remindmelater

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.app.ActivityCompat
import com.example.remindmelater.databinding.ActivityMainBinding
import com.example.remindmelater.databinding.ActivityMapsBinding

import com.example.remindmelater.service.ReminderServiceStub
import com.example.remindmelater.ui.theme.RemindMeLaterTheme
import com.example.remindmelater.ui.theme.UpdateReminderDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.runBlocking
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: View
    private val viewModel: MainViewModel by viewModel<MainViewModel>()
    private var userLatitude = 0.0
    private var userLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            RemindMeLaterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {

                    MainScreen()
                    isLocationPermissionGranted()
                    Map()
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        enableUserLocation(mMap)
        runBlocking { addSavedReminders() }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        RemindMeLaterTheme {
            MainScreen()
            ReminderRow()
        }
    }

    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        val openDialog = remember {mutableStateOf(false)}
        var isVisible by remember { mutableStateOf(true) }
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
            UpdateReminderDialog(openDialog)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {

                         openDialog.value = true

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

                        openDialog.value = true

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
                        Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show()
                        hideMap()
                        isVisible = true
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
//                        Toast.makeText(context, "You clicked the button", Toast.LENGTH_LONG).show()
                        isVisible = false
                        showMap()
                        moveMapToUser()
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
            if(isVisible) {
                ReminderRow()
            }
            Scaffold { innerPadding ->
                Column() {

                }
            }
        }
    }

    @Composable
    fun ReminderRow(){

        val reminders_ = remember { mutableStateListOf(Reminder()) }

        viewModel.fetchReminders(reminders_)

        Row(
            modifier = Modifier.padding(vertical = 200.dp)
        ) {
            LazyColumn() {
                items(reminders_) { item: Reminder ->
                    ReminderListItem(item, true)
                }
            }
        }
    }

    @Composable
    fun ReminderListItem(reminder: Reminder, isVisible: Boolean) {
        if(isVisible) {
            Log.d("Reminder List ", reminder.userEmail)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Column(
                    modifier = Modifier
                        .weight(6f)
                        .padding(0.dp)
                ) {
                    Text(text = "Reminder: ${reminder.body}")
                    Text(text = "Location: ")
                    Text(text = "For: ${reminder.userEmail}")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ){
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Edit, null)
                    }
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                }
            }        }
    }

    @Composable
    fun Map() {
        val binding = ActivityMapsBinding.inflate(layoutInflater)
        addContentView(binding.root, ViewGroup.LayoutParams(-1, -1))

//     Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)
    }

    fun hideMap() {
        mapView = findViewById(R.id.map_layout)
        mapView.visibility = View.INVISIBLE
    }

    fun showMap() {
        mapView = findViewById(R.id.map_layout)
        enableUserLocation(mMap)
        mapView.visibility = View.VISIBLE
    }

    // Adds a map marker with a label at the given lat and long.
    fun addMapMarker(label: String, lat: Double, long: Double) {
        val loc = LatLng(lat, long)
        mMap.addMarker(MarkerOptions().position(loc).title(label))
    }

    // Moves camera location to given lat and long
    fun moveMapCamera(lat: Double, long: Double) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, long)))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5f))
    }

    suspend fun addSavedReminders() {
        val savedReminders: List<Reminder>? = ReminderServiceStub().fetchReminders()
        savedReminders?.let {
            it.forEach { reminder ->
                addMapMarker(reminder.title, reminder.latitude.toDouble(), reminder.longitude.toDouble())
            }
        }
    }

    // Checks whether all location permissions are granted and returns true or false
    fun isLocationPermissionGranted(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    // Sends a permission request to the user for the needed location permissions
    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }

    //Gets users current location if available
    @SuppressLint("MissingPermission") //Permission is checked with isLocationPermissionGranted()
    fun getCurrentLocation(): Map<String, Double> {
        var token = CancellationTokenSource().token

        return if (isLocationPermissionGranted()) {
            fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, token)
                .addOnSuccessListener { loc ->
                    userLatitude = loc.latitude
                    userLongitude = loc.longitude
                }
            mapOf("latitude" to userLatitude, "longitude" to userLongitude)
        } else {
            requestLocationPermission()
            mapOf("latitude" to 10.0, "longitude" to 10.0)
        }
    }

    @SuppressLint("MissingPermission")
    fun enableUserLocation(map: GoogleMap) {
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    fun moveMapToUser() {
        var loc = getCurrentLocation()
        var lat = loc["latitude"]
        var long = loc["longitude"]
        moveMapCamera(lat!!, long!!)
    }
}