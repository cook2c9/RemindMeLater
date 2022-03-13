package com.example.remindmelater.dao

import com.example.remindmelater.dto.Reminder
import retrofit2.Call
import retrofit2.http.GET

interface IReminderDAO {

    @GET("/perl/mobile/viewplantsjsonarray.pl")
    fun getAllReminders() : Call<ArrayList<Reminder>>
}