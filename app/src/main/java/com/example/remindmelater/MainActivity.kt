package com.example.remindmelater

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.remindmelater.databinding.ActivityMapsBinding
import com.example.remindmelater.ui.theme.RemindMeLaterTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemindMeLaterTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                    Map()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title(getString(R.string.marker_in_sydney)))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        val clicked = stringResource(R.string.clicked)
        val remindMeLater = stringResource(R.string.remind_me_later)
        val reminderList = stringResource(R.string.reminder_list)
        val mapViewLabel = stringResource(R.string.map_view_label)
        val others = stringResource(R.string.others)
        Column {
            TopAppBar(
                elevation = 4.dp,
                title = {
                    Text(remindMeLater)
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
                text = stringResource(R.string.greeting),
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, clicked, Toast.LENGTH_LONG).show()
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
                    Text(text = stringResource(R.string.myself))
                }
                Button(
                    onClick = {
                        Toast.makeText(context, clicked, Toast.LENGTH_LONG).show()
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
                    Text(text = others)
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        Toast.makeText(context, clicked, Toast.LENGTH_LONG).show()
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
                    Text(text = reminderList)
                }
                Button(
                    onClick = {
                        Toast.makeText(context, clicked, Toast.LENGTH_LONG).show()
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
                    Text(text = mapViewLabel)
                }
            }
            Scaffold { innerPadding ->
                LazyColumn(contentPadding = innerPadding) {

                }
            }

        }

        @Composable
        fun ReminderListItem() {
            val remind = stringResource(R.string.remind)
            val location = stringResource(R.string.location)
            val forText = stringResource(R.string.for_text)
            Column {
                Text(text = "$remind:")
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(7.dp)
                ) {
                    Text(text = "$location:")
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
                    Text(text = "$forText:")
                }
            }

        }

    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        RemindMeLaterTheme {
            MainScreen()
        }
    }

    @Composable
    fun Map() {
        Box()
        {
            val binding = ActivityMapsBinding.inflate(layoutInflater)
            addContentView(binding.root, ViewGroup.LayoutParams(-1, -1))

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@MainActivity)
        }
    }
}