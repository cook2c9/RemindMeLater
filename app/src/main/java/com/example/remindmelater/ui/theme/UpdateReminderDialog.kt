package com.example.remindmelater.ui.theme

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.remindmelater.R
import com.example.remindmelater.dto.Reminder
import com.example.remindmelater.MainViewModel

@Composable
fun UpdateReminderDialog(openDialog: MutableState<Boolean>, context: Context) {

    val geocoder = Geocoder(context)
    var strSelectedData = ""
    val reminder = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val title = remember { mutableStateOf("") }
    val userEmail = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester }

    fun addressAutoComplete(userInput: String): List<String> {
        return try {
            geocoder.getFromLocationName(userInput, 5).map { it.getAddressLine(0) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Composable
    fun TextFieldWithDropdown(
        modifier: Modifier = Modifier,
        value: TextFieldValue,
        setValue: (TextFieldValue) -> Unit,
        onDismissRequest: () -> Unit,
        dropDownExpanded: Boolean,
        list: List<String>,
        label: String = ""
    ) {
        Box(modifier) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (!focusState.isFocused)
                            onDismissRequest()
                    },
                value = value,
                onValueChange = setValue,
                label = { Text(label) },
                colors = TextFieldDefaults.outlinedTextFieldColors()
            )
            DropdownMenu(
                expanded = dropDownExpanded,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = onDismissRequest
            ) {
                list.forEach { text ->
                    DropdownMenuItem(onClick = {
                        setValue(
                            TextFieldValue(
                                text,
                                TextRange(text.length)
                            )
                        )
                    }) {
                        Text(text = text)
                    }
                }
            }
        }
    }

    @Composable
    fun TextFieldWithDropdownUsage() {

        val dropDownOptions = remember { mutableStateOf(listOf<String>()) }
        val textFieldValue = remember { mutableStateOf(TextFieldValue()) }
        val dropDownExpanded = remember { mutableStateOf(false) }

        fun onDropdownDismissRequest() {
            dropDownExpanded.value = false
        }

        fun onValueChanged(value: TextFieldValue) {
            strSelectedData = value.text
            dropDownExpanded.value = true
            textFieldValue.value = value
            dropDownOptions.value = addressAutoComplete(strSelectedData)
        }


        TextFieldWithDropdown(
            modifier = Modifier.fillMaxWidth(0.8f),
            value = textFieldValue.value,
            setValue = ::onValueChanged,
            onDismissRequest = ::onDropdownDismissRequest,
            dropDownExpanded = dropDownExpanded.value,
            list = dropDownOptions.value,
            label = "Location"
        )
    }

    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(580.dp)
                    .padding(5.dp),
                shape = RoundedCornerShape(5.dp),
                color = Color.LightGray
            ) {
                Column(
                    modifier = Modifier.padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Add/Update Reminder",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_create_24),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .width(120.dp)
                            .height(120.dp)
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = reminder.value,
                        onValueChange = { reminder.value = it },
                        label = { Text(text = "Reminder") },
                        placeholder = { Text(text = "Reminder...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    TextFieldWithDropdownUsage()
//                    OutlinedTextField(
//                        value = location.value,
//                        onValueChange = { location.value = it },
//                        label = { Text(text = "location") },
//                        placeholder = { Text(text = "location") },
//                        singleLine = true,
//                        modifier = Modifier.fillMaxWidth(0.8f)
//                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = title.value,
                        onValueChange = { title.value = it },
                        label = { Text(text = "Title") },
                        placeholder = { Text(text = "Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = userEmail.value,
                        onValueChange = { userEmail.value = it },
                        label = { Text(text = "Email Address") },
                        placeholder = { Text(text = "Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.padding(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {saveNewReminder(reminder.value, location.value, title.value, userEmail.value)}
                        ) {
                            Icon(Icons.Filled.Check, null, tint = Color(5, 115, 34))
                        }
                        IconButton(onClick = {openDialog.value = false}) {
                            Icon(Icons.Filled.Close, null, tint = Color.Red)
                        }

                    }
                }
            }
        }
    }
}

fun saveNewReminder(reminder: String, location : String, newTitle: String, email: String) {
    val viewModel = MainViewModel()
    val newReminder = Reminder()
    newReminder.geoID = "sfdesfdedfa"
    newReminder.longitude = location.toDouble()
    newReminder.latitude = location.toDouble()
    newReminder.body = reminder
    newReminder.title = newTitle
    newReminder.userEmail = email
    newReminder.radius = 0
    viewModel.saveReminders(newReminder)
}

