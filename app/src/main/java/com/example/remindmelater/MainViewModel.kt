package com.example.remindmelater

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.service.ReminderService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var reminders : MutableLiveData<List<Reminder>> = MutableLiveData<List<Reminder>>()
    var reminderService : ReminderService = ReminderService()

    fun fetchReminders(){
        viewModelScope.launch{
            var innerReminder = reminderService.fetchReminders()
            reminders.postValue(innerReminder)
        }
    }
}