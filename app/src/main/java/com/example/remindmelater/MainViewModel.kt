package com.example.remindmelater

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.remindmelater.ReminderRecyclerView.ReminderAdapter
import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.service.IReminderService
import com.example.remindmelater.service.ReminderService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.CompletableDeferred

class MainViewModel(var reminderService : IReminderService = ReminderService()) : ViewModel() {

    internal val NEW_REMINDER = "New Reminder"
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    var reminders : MutableLiveData<List<Reminder>> = MutableLiveData<List<Reminder>>()
    var selectedReminder by mutableStateOf(Reminder())
    private lateinit var reminderAdapter: ReminderAdapter

    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        auth = FirebaseAuth.getInstance()
    }

    private fun listenToReminders() {
        firestore.collection("reminders").addSnapshotListener {
                snapshot, e ->
            // Handle error
            if(e != null){
                Log.w("Listen failed", e)
                return@addSnapshotListener
            }
            // Find the data
            snapshot?.let{
                val allReminders = ArrayList<Reminder>()
                val documents = snapshot.documents
                documents.forEach{
                    val reminder = it.toObject(Reminder::class.java)
                    if(reminder != null) {
                        reminder?.let {
                            allReminders.add(it)
                        }
                    }
                }
                reminders.value = allReminders
            }
        }
    }

    fun fetchReminders(reminders: SnapshotStateList<Reminder>){
        var currentUser = auth.currentUser

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

    suspend fun getUserReminders(userID: String) : List<Reminder>? {
        val def = CompletableDeferred<List<Reminder>?>()
        firestore.collection("reminders").get().addOnSuccessListener {
            def.complete(it.toObjects(Reminder::class.java))
            }
        return def.await()
    }

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

    fun saveReminders(reminder: Reminder){
        val document =  if (reminder.documentID == null || reminder.documentID.isEmpty()) {
            firestore.collection("reminders").document()
        }
        else {
            firestore.collection("reminders").document(reminder.documentID!!)
        }
        reminder.documentID = document.id
        MainActivity().addMapMarker(reminder.documentID, reminder.title, reminder.latitude, reminder.longitude)
//        MainActivity().createGeofence(reminder.documentID, reminder.latitude, reminder.longitude, reminder.radius.toFloat())
//        MainActivity().addGeofences()
        val handle = document.set(reminder)
        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
        handle.addOnFailureListener { Log.e("Firebase", "Save Failed")}
    }

    fun deleteReminder(documentID: String) {
        firestore.collection("reminders").document(documentID).delete()
    }
}
