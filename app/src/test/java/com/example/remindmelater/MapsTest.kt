package com.example.remindmelater

import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.service.ReminderService
import org.junit.Test
import org.junit.Assert.*

class MapsTest {

    lateinit var reminderService : ReminderService
    var allReminders : List<Reminder>? = ArrayList()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    suspend fun `given reminder data when the app is launched then a map marker for each reminder should be created`(){
        givenReminderServiceIsInitialized()
        whenAppLaunched()
        thenCreateMapMarkers()
    }

    private fun givenReminderServiceIsInitialized() {
       reminderService = ReminderService()
    }

    private suspend fun whenAppLaunched() {
        //allReminders = reminderService.getReminders()
    }

    private fun thenCreateMapMarkers() {
        assertNotNull(allReminders)
        assertTrue(allReminders!!.isNotEmpty())
        TODO("Finish creating test")
    }
}