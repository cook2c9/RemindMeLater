package com.example.remindmelater.dto

data class Reminder(var title : String = "", var body: String = "", var latitude : Float = 0.0f, var longitude : Float = 0.0f, var radius : Int = 0, var userEmail : String = "") {

}
