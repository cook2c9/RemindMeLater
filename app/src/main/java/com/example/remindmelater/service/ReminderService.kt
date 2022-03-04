package com.example.remindmelater.service

import com.example.remindmelater.RetrofitClientInstance
import com.example.remindmelater.dao.IReminderDAO
import com.example.remindmelater.dto.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class ReminderService {
    suspend fun fetchReminders() : List<Reminder>? {
        return withContext(Dispatchers.IO){
            val service = RetrofitClientInstance.retrofitInstance?.create(IReminderDAO::class.java)
            var reminders = async {service?.getAllReminders()}
            var result = reminders.await()?.awaitResponse()?.body()
            return@withContext result
        }
    }
}