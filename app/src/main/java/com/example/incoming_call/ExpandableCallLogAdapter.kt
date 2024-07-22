package com.example.incoming_call

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.incoming_call.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import java.util.Locale

class ExpandableCallLogAdapter(
    private val groupedCallLogs: List<GroupedCallLogEntry>
) : RecyclerView.Adapter<ExpandableCallLogAdapter.GroupedCallLogViewHolder>() {

    private val expandedItems = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupedCallLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grouped_call_log_item, parent, false)
        return GroupedCallLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupedCallLogViewHolder, position: Int) {
        val groupedCallLogEntry = groupedCallLogs[position]
        holder.bind(groupedCallLogEntry, position, expandedItems.contains(position))
        holder.itemView.setOnClickListener {
            if (expandedItems.contains(position)) {
                expandedItems.remove(position)
            } else {
                expandedItems.add(position)
            }
            notifyItemChanged(position)

            // Launch the DetailedCallLogActivity
            val context = it.context
            val intent = Intent(context, DetailedCallLogActivity::class.java).apply {
                putExtra("number", groupedCallLogEntry.number)
                putExtra("callType", groupedCallLogEntry.callType)
                putParcelableArrayListExtra("callLogs", ArrayList(groupedCallLogEntry.callLogs))
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return groupedCallLogs.size
    }

    private fun Int.toFormattedDuration(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    inner class GroupedCallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberTextView: TextView = itemView.findViewById(R.id.callNumber)
        private val callTypeTextView: TextView = itemView.findViewById(R.id.callType)
        private val callLogsContainer: ViewGroup = itemView.findViewById(R.id.callLogsContainer)

        fun bind(groupedCallLogEntry: GroupedCallLogEntry, position: Int, isExpanded: Boolean) {
            numberTextView.text = groupedCallLogEntry.number
            callTypeTextView.text = groupedCallLogEntry.callType

            callLogsContainer.removeAllViews()

            //I comment this -
            /* if (isExpanded) {
                groupedCallLogEntry.callLogs.forEach { callLogEntry ->
                    val logView = LayoutInflater.from(itemView.context).inflate(R.layout.call_log_item, callLogsContainer, false)
                    logView.findViewById<TextView>(R.id.callDate).text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(callLogEntry.date))
                    logView.findViewById<TextView>(R.id.callDuration).text = callLogEntry.duration.toFormattedDuration()
                    logView.findViewById<TextView>(R.id.callNumber).text = callLogEntry.number
                    callLogsContainer.addView(logView)
                }
                callLogsContainer.visibility = View.VISIBLE
            } else {
                callLogsContainer.visibility = View.GONE
            }*/
        }
    }
}
