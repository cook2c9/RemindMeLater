package com.example.remindmelater

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.remindmelater.MainActivity.Companion.showNotification
import com.example.remindmelater.dto.Reminder
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.*

//Watches for any geofences being tripped
class GeofenceBroadcastReceiver(): BroadcastReceiver() {

    //When the receiver sees a geofence has been tripped it will get the geofences and use it to create a notification
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }
        var reminder: Reminder?
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("Error", errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences
            goAsync(GlobalScope, Dispatchers.Main) {
                triggeringGeofences.let {
                    it.forEach { geofence ->
                        reminder = MainViewModel().getDocument(geofence.requestId)
                        reminder?.let { it -> showNotification(context!! , it.title, it.body) }
                    }
                }
            }
            Log.i("Geofence", "Entered")
        }
    }

    private fun BroadcastReceiver.goAsync(
        coroutineScope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        block: suspend () -> Unit
    ) {
        val pendingResult = goAsync()
        coroutineScope.launch(dispatcher) {
            block()
            pendingResult.finish()
        }
    }
}