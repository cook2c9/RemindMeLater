package com.example.remindmelater.service

import com.example.remindmelater.dto.Reminder

class ReminderServiceStub : IReminderService {
    override fun getReminders(): List<Reminder>? {
        val remOne = Reminder(1,"Pick up paper towels",39.103,-84.512,"Myself")
        val remTwo = Reminder(2,"Drop off charge cord I left in your car",39.9,-89.28,"Dad")
        val remThree = Reminder(3,"Pick up Nails",40.289,-79.8178,"Myself")
        val remFour = Reminder(4,"Look for new book for Matt",39.0,-84.0,"Myself", 5)
        return arrayListOf(remOne, remTwo, remThree, remFour)
    }

}