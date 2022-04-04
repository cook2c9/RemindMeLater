package com.example.remindmelater

import com.example.remindmelater.service.ReminderService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel() }
    single{ ReminderService() }
}