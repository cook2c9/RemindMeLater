package com.example.remindmelater

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.remindmelater.dto.Reminder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.CompletableDeferred

class MainViewModel() : ViewModel() {

    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    var reminders : MutableLiveData<List<Reminder>> = MutableLiveData<List<Reminder>>()

    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        auth = FirebaseAuth.getInstance()
    }

    fun fetchReminders(reminders: SnapshotStateList<Reminder>){
        val currentUser = auth.currentUser

        currentUser?.let {
            Log.d("Reminder Found For User",it.uid)
            firestore.collection("reminders").whereEqualTo("userID", it.uid).get()
                .addOnSuccessListener {
                reminders.updateList(it.toObjects(Reminder::class.java))
            }.addOnFailureListener{
                reminders.updateList(listOf())
            }
        }
    }
    //Extention function of the one above, used to clear or add the List of reminders
    private fun <T> SnapshotStateList<T>.updateList(reminderList: List<T>) {
        clear()
        addAll(reminderList)
    }

    // Gets the signed in users reminders from firebase
    suspend fun getUserReminders() : List<Reminder>? {
        val def = CompletableDeferred<List<Reminder>?>()
        val user = auth.currentUser
            firestore.collection("reminders").whereEqualTo("userID", user?.uid).get()
                .addOnSuccessListener {
                    def.complete(it.toObjects(Reminder::class.java))
                }
        return def.await()
    }

    // Gets a single reminder from Firebase using the documentID
    suspend fun getDocument(documentID: String) : Reminder? {
        val def = CompletableDeferred<Reminder?>()
        firestore.collection("reminders")
            .document(documentID)
            .get()
            .addOnSuccessListener {
                def.complete(it.toObject(Reminder::class.java))
        }
        return def.await()
    }

    //Saves a reminder to firebase, also creates a map marker and geofence for the reminder.
    fun saveReminders(reminder: Reminder){
        val document =  if (reminder.geoID == null || reminder.geoID.isEmpty()) {
            firestore.collection("reminders").document()
        }
        else {
            firestore.collection("reminders").document(reminder.geoID!!)
        }
        reminder.geoID = document.id
        MainActivity().addMapMarker(reminder.geoID, reminder.title, reminder.latitude, reminder.longitude)
        MainActivity().createGeofence(reminder.geoID, reminder.latitude, reminder.longitude, reminder.radius.toFloat())
        MainActivity().addGeofences()
        val handle = document.set(reminder)
        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
        handle.addOnFailureListener { Log.e("Firebase", "Save Failed")}
    }

    //Deletes a reminder from firebase, removes the map marker, and removes the geofence use the documentID
    fun deleteReminder(documentID: String) {
        firestore.collection("reminders").document(documentID).delete()
        MainActivity().removeMapMarker(documentID)
        MainActivity().removeGeofence(documentID)
    }

    fun updateReminder(documentID: String, reminder: Reminder){
        reminder.geoID = documentID
        firestore.collection("reminders").document(documentID)
            .set(reminder)
    }

    fun checkIfReminderExists(documentID: String, reminder: Reminder){
        Log.d("Document", "Is not null ${documentID}")
        if(!documentID.equals(""))
            firestore.collection("reminders").document(documentID)
                .get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document.exists()) {
                            updateReminder(documentID, reminder)
                            Log.d("TAG", "Document already exists. $documentID")
                        }
                    } else {
                        Log.d("An Error Occurred", "Try again")
                    }
                }
        else {
            saveReminders(reminder)
            Log.d("TAG", "Document does NOT exist. $documentID")
        }
    }
}
