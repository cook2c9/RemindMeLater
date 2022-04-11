package com.example.remindmelater

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.remindmelater.ui.theme.RemindMeLaterTheme

@Composable
fun ReminderListItem() {


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Reminder:")
            Text(text = "For:")
            Text(text = "Location:")
            Text(text = "Range:")

            Column() {
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Check, null)
                    }
                    IconButton(onClick = {/* Do Something*/ }) {
                        Icon(Icons.Filled.Delete, null)
                    }
                }
            }

        }

    }




@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    RemindMeLaterTheme {
        ReminderListItem()
    }
}