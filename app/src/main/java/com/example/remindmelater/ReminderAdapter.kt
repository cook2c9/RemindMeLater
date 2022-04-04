package com.example.remindmelater.ReminderRecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.remindmelater.R
import com.example.remindmelater.dto.Reminder

class ReminderAdapter(private val reminderList : ArrayList<Reminder>) : RecyclerView.Adapter<ReminderAdapter.ReminderVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderAdapter.ReminderVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_reminders,parent,false)

        return ReminderVH(itemView)
    }

    override fun onBindViewHolder(holder: ReminderAdapter.ReminderVH, position: Int) {
        val reminder = reminderList[position]
        holder.reminderTitle.text = reminder.title
        holder.reminderBody.text = reminder.body
        holder.reminderFor.text = reminder.userEmail
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    public class ReminderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderTitle : TextView = itemView.findViewById<TextView>(R.id.tvReminderTitle)
        val reminderBody : TextView = itemView.findViewById<TextView>(R.id.tvReminderBody)
        val reminderFor : TextView = itemView.findViewById<TextView>(R.id.tvReminderFor)
    }
}