package com.example.remindmelater.dto

import retrofit2.http.Body

data class Reminder(var title : String = "", var body: String = "", var latitude : Float = 0.0f, var longitude : Float = 0.0f, var radius : Int = 0, var userEmail : String = "") {
    override fun toString(): String {
        return "$title $body $userEmail"
    }
}
