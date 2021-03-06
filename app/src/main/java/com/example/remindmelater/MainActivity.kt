package com.example.remindmelater

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.remindmelater.databinding.ActivityMapsBinding
import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.ui.theme.RemindMeLaterTheme
import com.example.remindmelater.ui.theme.UpdateReminderDialog
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.compose.material.Icon
import androidx.compose.ui.res.stringResource
import com.example.remindmelater.ui.theme.ButtonBlue
import com.example.remindmelater.ui.theme.MainTeal

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: View
    private lateinit var notificationManager: NotificationManager
    private val user = FirebaseAuth.getInstance().currentUser
    private val viewModel: MainViewModel by viewModel()

    //Checks if the location permissions are granted and if they are enables the user location on the map and adds the saved reminders as geofences
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            isGranted ->
        if(isGranted.all { it.value }) {
            enableUserLocation(mMap)
            lifecycleScope.launch { addSavedRemindersGeofences() }
        } else {
            Toast.makeText(this, "Location Permission Needed", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this.applicationContext)
        staticContext = this.applicationContext

        setContent {
            RemindMeLaterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                    Map()
                    createNotificationChannel()
                    requestLocationPermission()
                }
            }
        }
    }

    // When the map is ready enables user location marker and adds the saved reminders as markers.
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        enableUserLocation(mMap)
        lifecycleScope.launch { addSavedRemindersMarkers() }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        RemindMeLaterTheme {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        val openDialog = remember { mutableStateOf(false) }
        var isVisible by remember { mutableStateOf(true) }
        Column {
            TopAppBar(
                elevation = 4.dp,
                title = {
                    Text(stringResource(R.string.RemindMeLater))
                },
                backgroundColor = MainTeal,
                navigationIcon = {
                    IconButton(onClick = {signOut()}) {
                        Icon(Icons.Filled.Logout, null)
                    }
                }, actions = {
                    IconButton(onClick = {
                        Log.d("Button", "Pushed")
                    }) {
                        Icon(Icons.Filled.Settings, null)
                    }
                })

            Text(
                text = stringResource(R.string.Greeting) + (user?.email ?: stringResource(R.string.User)),
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
            )
            UpdateReminderDialog(openDialog, "")
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
                        .width(380.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = ButtonBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = "Myself")
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Log.d("MESSAGE: ", "Reminder List Button Clicked")
                        hideMap()
                        isVisible = true
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .height(35.dp)
                        .width(190.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = ButtonBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(R.string.ReminderList))
                }
                Button(
                    onClick = {
                        Log.d("MESSAGE: ", "Map View Button Clicked")
                        isVisible = false
                        showMap()
                        enableUserLocation(mMap)
                        lifecycleScope.launch { moveMapToUser() }
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .height(35.dp)
                        .width(190.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = ButtonBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(R.string.MapView))
                }
            }
            if (isVisible) {
                ReminderRow()
            }
            Scaffold {
                Column {

                }
            }
        }
    }

    @Composable
    fun ReminderRow() {

        val reminders = remember { mutableStateListOf(Reminder()) }

        viewModel.fetchReminders(reminders)

        Row(
            modifier = Modifier
                .size(width = 400.dp, height = 500.dp)
                .padding(top = 10.dp)

        ) {
            LazyColumn {
                items(reminders) { item: Reminder ->
                    ReminderListItem(item)
                }
            }
        }
    }

    @Composable
    fun ReminderListItem(reminder: Reminder) {
        val openDialog = remember { mutableStateOf(false) }
        val isVisible by remember { mutableStateOf(true) }

        if (isVisible) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                elevation = 8.dp,
                backgroundColor = Color.LightGray,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.Black)
            )
            {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(6f)
                            .padding(start = 2.dp)
                    ) {
                        Text(text = "Reminder: ${reminder.body}", fontWeight = FontWeight.Bold)
                        Text(text = "Location: ${reminder.latitude} | ${reminder.longitude}")
                        Text(text = "For: ${reminder.userID}")
                        Text(text = "Range: ${reminder.radius}")
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Button(
                            onClick = { openDialog.value = true },
                            modifier = Modifier
                                .padding(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = ButtonBlue
                            )
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier
                                    .background(color = ButtonBlue)
                            )
                        }
                        UpdateReminderDialog(openDialog, reminder.geoID)
                        Button(
                            onClick = {
                                viewModel.deleteReminder(reminder.geoID)
                            },
                            modifier = Modifier
                                .padding(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = ButtonBlue
                            )
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .background(color = ButtonBlue)
                            )
                        }
                    }
                }
            }
        }
    }

    // Creates the map in the app
    @Composable
    fun Map() {
        val binding = ActivityMapsBinding.inflate(layoutInflater)
        addContentView(binding.root, ViewGroup.LayoutParams(-1, -1))

//     Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Hides the map in the app.
    private fun hideMap() {
        mapView = findViewById(R.id.map_layout)
        mapView.visibility = View.INVISIBLE
    }

    // Sets the map to be visible in the app
    private fun showMap() {
        mapView = findViewById(R.id.map_layout)
        mapView.visibility = View.VISIBLE
    }

    // Adds a map marker with a label at the given lat and long.  ALso adds the markers to a list so they can be removed
    internal fun addMapMarker(id: String, label: String, lat: Double, long: Double) {
        val loc = LatLng(lat, long)
        val tempMarker = mMap.addMarker(MarkerOptions().position(loc).title(label))
        tempMarker?.let { markerList.put(id, it) }
    }

    //Removes a map marker using the reminder's documentID from Firebase
    fun removeMapMarker(id: String) {
        val marker = markerList[id]
        marker?.remove()
        markerList.remove(id)
    }

    // Moves camera location to given lat and long
    private fun moveMapCamera(lat: Double, long: Double) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(lat, long)))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5f))
    }

    // Adds all the signed in user's reminders from Firebase to map markers
    private suspend fun addSavedRemindersMarkers() {
        val savedReminders: List<Reminder>? = MainViewModel().getUserReminders()
        savedReminders?.let { reminder ->
            reminder.forEach { addMapMarker(it.geoID, it.title, it.latitude, it.longitude) }
        }
    }

    // Adds all the signed in user's reminders from Firebase to geofences
    private suspend fun addSavedRemindersGeofences() {
        val savedReminders = MainViewModel().getUserReminders()
        savedReminders?.let { reminder ->
            reminder.forEach {
                createGeofence(it.geoID, it.latitude, it.longitude, it.radius.toFloat())
                Log.i("GeoID", it.geoID)
            }
        }
        if (geofenceList.isNotEmpty()) {
            addGeofences()
        }
    }

    // Sends a permission request to the user for the needed location permissions
    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    //Gets users last known location and returns when it has been successful
    @SuppressLint("MissingPermission") //Permission is checked with isLocationPermissionGranted()
    internal suspend fun getLastLocation(): Location? {
        val def = CompletableDeferred<Location>()

        return if (isLocationPermissionGranted()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { loc ->
                    def.complete(loc)
                }
            def.await()
        } else {
            null
        }
    }

    // Enables a marker on the map that shows the user's location
    @SuppressLint("MissingPermission") // Permission checked with isLocationPermissionGranted
    fun enableUserLocation(map: GoogleMap) {
        if (isLocationPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
    }

    // Uses the users gps location and moves the map's camera to that spot on the map
    private suspend fun moveMapToUser() {
        val loc = getLastLocation()
        loc?.let { moveMapCamera(loc.latitude, loc.longitude) }
    }

    //Creates a request that indicates when the geofences should trigger and adds them to the request
    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    //Creates a new Geofence using the builder and adds it to the geofenceList
    internal fun createGeofence(id: String, lat: Double, long: Double, radius: Float = 300f) {
        geofenceList.add(
            Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(id)

                // Set the circular region of this geofence.
                .setCircularRegion(
                    lat,
                    long,
                    radius
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Geofence.NEVER_EXPIRE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                // Create the geofence.
                .build()
        )
    }

    //Adds all the Geofences in the geofence list
    @SuppressLint("MissingPermission") //Permission is checked with isLocationPermissionGranted()
    internal fun addGeofences() {
        if (isLocationPermissionGranted()) {
            geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)
            Log.i("Geofence", "Added")
        }
    }

    //Creates a channel for the notifications to be sent through
    private fun createNotificationChannel() {
        val name = "Notification"
        val descriptionText = "Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNELID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    //Removes a geofence from the list using the Firebase documentID of the reminder
    fun removeGeofence(documentID: String) {
        geofenceList.removeIf { it.requestId == documentID }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val loginScreen = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(loginScreen)
    }

    companion object {
        lateinit var mMap: GoogleMap
        @SuppressLint("StaticFieldLeak")
        lateinit var staticContext: Context
        @SuppressLint("StaticFieldLeak")
        lateinit var geofencingClient: GeofencingClient
        private const val CHANNELID = "1"
        var markerList = HashMap<String, Marker>()
        var geofenceList = mutableListOf<Geofence>()

        val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(staticContext, GeofenceBroadcastReceiver::class.java)
            // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
            // addGeofences() and removeGeofences().
            PendingIntent.getBroadcast(staticContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // Creates a notification using the builder and shows the notification
        fun showNotification(con: Context, title: String, content: String) {

            val builder = NotificationCompat.Builder(con, "1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(con))
            {
                notify(1, builder.build())
            }
        }

        // Checks whether all location permissions are granted and returns true or false
        fun isLocationPermissionGranted(): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    staticContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    staticContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
        }
    }
}