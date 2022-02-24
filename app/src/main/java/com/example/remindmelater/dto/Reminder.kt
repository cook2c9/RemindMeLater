package com.example.remindmelater.dto

data class Reminder(var reminderId : Int, var reminder: String, var latitude : Float, var longitude : Float, var radius : Int, var person : String) {

}
