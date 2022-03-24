package com.example.remindmelater

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.service.IReminderService
import com.example.remindmelater.service.ReminderService
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(var reminderService : IReminderService = ReminderService()) : ViewModel() {

    internal val NEW_REMINDER = "New Reminder"
    private lateinit var firestore : FirebaseFirestore
    var reminders : MutableLiveData<List<Reminder>> = MutableLiveData<List<Reminder>>()
    var selectedReminder by mutableStateOf(Reminder())

    init{
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
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

    fun fetchReminders(){
        firestore.collection("reminders")
            .get()
            .addOnSuccessListener { result ->
                val results: ArrayList<String> = ArrayList()
                for (document in result) {
                    val foundReminder = document.toObject(Reminder::class.java).toString()
                    Log.d(TAG, "Document Found: $foundReminder")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun saveReminders(reminder: Reminder){
        val document =  if (reminder.title == null) {
            firestore.collection("reminders").document()
        }
        else {
            firestore.collection("reminders").document(reminder.title)
        }
        reminder.title = document.id
        val handle = document.set(reminder)
        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
        handle.addOnFailureListener { Log.e("Firebase", "Save Failed")}
    }
}