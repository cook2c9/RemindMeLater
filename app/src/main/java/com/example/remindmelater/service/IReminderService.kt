package com.example.remindmelater.service

import com.example.remindmelater.dto.Reminder

interface IReminderService {
    suspend fun getReminders() : List<Reminder>?
}