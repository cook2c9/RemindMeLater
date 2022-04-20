package com.example.remindmelater

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.remindmelater.dto.Reminder
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ReminderTests {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


}