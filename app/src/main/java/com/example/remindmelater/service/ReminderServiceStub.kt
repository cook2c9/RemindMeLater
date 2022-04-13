package com.example.remindmelater.service

import com.example.remindmelater.dto.Reminder

class ReminderServiceStub : IReminderService {
    override suspend fun fetchReminders(): List<Reminder> {
            val remOne = Reminder("387y321asd","Pick up", "Pick up paper towels", 39.103, -84.512, 1)
            val remTwo = Reminder("8273sajdsaj1", "Drop off", "Drop off charge cord I left in your car", 39.9, -89.28, 1)
            val remThree = Reminder("81bhsdasb83mn", "Pick up", "Pick up Nails", 40.289, -79.8178, 1)
            val remFour = Reminder("81gndas87n23", "Look for", "Look for new book for Matt", 39.0, -84.0, 1)
            return arrayListOf(remOne, remTwo, remThree, remFour)
        }
    }