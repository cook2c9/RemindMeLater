package com.example.remindmelater.service

import com.example.remindmelater.dto.Reminder

class ReminderServiceStub : IReminderService {
    override suspend fun fetchReminders(): List<Reminder> {
            val remOne = Reminder("Pick up", "Pick up paper towels", 39.103, -84.512, 1)
            val remTwo = Reminder("Drop off", "Drop off charge cord I left in your car", 39.9, -89.28, 1)
            val remThree = Reminder("Pick up", "Pick up Nails", 40.289, -79.8178, 1)
            val remFour = Reminder("Look for", "Look for new book for Matt", 39.0, -84.0, 1)
            return arrayListOf(remOne, remTwo, remThree, remFour)
        }
    }