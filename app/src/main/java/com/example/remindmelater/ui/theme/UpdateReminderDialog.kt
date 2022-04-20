package com.example.remindmelater.ui.theme

import android.location.Address
import android.location.Geocoder
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import com.example.remindmelater.MainViewModel
import com.example.remindmelater.R
import com.example.remindmelater.dto.Reminder
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UpdateReminderDialog(openDialog: MutableState<Boolean>, documentID: String) {

    val context = LocalContext.current
    val geocoder = Geocoder(context)
    var strSelectedData = ""
    val reminderBody = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val reminderTitle = remember { mutableStateOf("") }
    val reminderUser = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester }
    //val viewModel: MainViewModel by viewModel<MainViewModel>()
    val auth = FirebaseAuth.getInstance()

    // Returns the address line of the top 5 geocoder results from a String
    fun addressAutoComplete(userInput: String): List<String> {
        return try {
            geocoder.getFromLocationName(userInput, 5).map { it.getAddressLine(0) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Returns the first Address from the geocoder using a string input
    fun addressLookup(input: String): Address? {
        return try {
            geocoder.getFromLocationName(input, 1).first()
        } catch (e: Exception) {
            null
        }

    }

    //Creates a textfield that can create a dropdown when provided a list of strings
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

        //When the focus is off the dropdown hide it
        fun onDropdownDismissRequest() {
            dropDownExpanded.value = false
        }

        //When the value changes in the Textfield
        fun onValueChanged(value: TextFieldValue) {
            strSelectedData = value.text
            dropDownExpanded.value = true
            textFieldValue.value = value
            dropDownOptions.value = addressAutoComplete(strSelectedData)
        }

        //Creates the actuall textfield
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
                        value = reminderBody.value,
                        onValueChange = { reminderBody.value = it },
                        label = { Text(text = "Reminder") },
                        placeholder = { Text(text = "Reminder...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    TextFieldWithDropdownUsage()

                    Spacer(modifier = Modifier.padding(10.dp))

                    OutlinedTextField(
                        value = reminderTitle.value,
                        onValueChange = { reminderTitle.value = it },
                        label = { Text(text = "Title") },
                        placeholder = { Text(text = "Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    Spacer(modifier = Modifier.padding(15.dp))
                    Row(
                        horizontalArrangement = Arrangement.End
                    ) {

                        IconButton(onClick = {
                            //When the reminder is saved the string in the location will be used to get the address.
                          val selectedAddress = addressLookup(strSelectedData)
                            // Using the address if it isn't null all the values will be used to create a reminder
                          selectedAddress?.let {
                            val reminder = Reminder().apply {
                                body = reminderBody.value
                                title = reminderTitle.value
                                latitude = selectedAddress.latitude
                                longitude = selectedAddress.longitude
                                userID = auth.currentUser?.uid
                            }
                              MainViewModel().checkIfReminderExists(documentID, reminder)
                              openDialog.value = false
                              //If a location is not found from the user input it will not save and notify the user a location is needed.
                          } ?: Toast.makeText(context,"A location needs to be selected", Toast.LENGTH_SHORT).show()

                        }

                        ) {
                            //Save Reminder Button
                            Icon(Icons.Filled.Check, null, tint = Color(5, 115, 34))
                        }
                        //Close Window/Cancel
                        IconButton(onClick = {openDialog.value = false}) {

                            Icon(Icons.Filled.Close, null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}


