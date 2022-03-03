package com.example.remindmelater.service

import com.example.remindmelater.dto.Reminder

class ReminderServiceStub : IReminderService {
    override suspend fun getReminders(): List<Reminder>? {
        TODO("Not yet implemented")
    }

}