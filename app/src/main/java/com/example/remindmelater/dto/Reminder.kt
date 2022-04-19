package com.example.remindmelater.dto

data class Reminder(var geoID: String? = null, var title : String = "", var body: String = "", var latitude : Double = 0.0, var longitude : Double = 0.0, var radius : Int = 0, var userID : String? = "") {
    override fun toString(): String {
        return "$title $body $userID"
    }
}