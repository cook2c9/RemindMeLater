package com.example.remindmelater.service

import com.example.remindmelater.dto.Reminder

interface IReminderService {
    fun getReminders() : List<Reminder>?
}