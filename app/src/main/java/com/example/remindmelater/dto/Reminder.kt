package com.example.remindmelater.dto

data class Reminder(var id : String = "", var title : String = "", var body: String = "", var latitude : Double = 0.0, var longitude : Double = 0.0, var radius : Int = 0, var userEmail : String = "") {

}
