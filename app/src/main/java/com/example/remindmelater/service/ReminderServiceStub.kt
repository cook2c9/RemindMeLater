package com.example.remindmelater.service

import com.example.remindmelater.dto.Reminder

class ReminderServiceStub : IReminderService {
    override suspend fun fetchReminders(): List<Reminder> {
            val remOne = Reminder("Pick up", "Pick up paper towels", 39.103f, -84.512f, 1)
            val remTwo = Reminder("Drop off", "Drop off charge cord I left in your car", 39.9f, -89.28f, 1)
            val remThree = Reminder("Pick up", "Pick up Nails", 40.289f, -79.8178f, 1)
            val remFour = Reminder("Look for", "Look for new book for Matt", 39.0f, -84.0f, 1)
            return arrayListOf(remOne, remTwo, remThree, remFour)
        }
    }