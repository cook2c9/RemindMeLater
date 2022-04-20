package com.example.remindmelater

import org.junit.Assert.*
import org.junit.jupiter.api.Test

class MapsTest {


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    suspend fun `given 005GvDex87NyWE97KhQk when getting a document then should 005GvDex87NyWE97KhQk`(){
        val doc = MainViewModel().getDocument("005GvDex87NyWE97KhQk")
        assertSame("005GvDex87NyWE97KhQk", doc!!.geoID)
    }
}