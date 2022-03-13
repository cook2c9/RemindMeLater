package com.example.remindmelater.dto

data class Reminder(var reminderId : Int, var title: String, var latitude : Double, var longitude : Double, var person : String, var radius : Int = 0) {
}