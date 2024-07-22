package com.example.incoming_call

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.incoming_call.databinding.ActivityDetailedCallLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.cardview.widget.CardView
import android.widget.TextView


class DetailedCallLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedCallLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedCallLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from Intent
        val number = intent.getStringExtra("number") ?: "Unknown"
        val callType = intent.getStringExtra("callType") ?: "Unknown"
        val callLogs = intent.getParcelableArrayListExtra<CallLogEntry>("callLogs") ?: arrayListOf()

        // Set the data to views
        binding.detailNumber.text = number
        binding.detailType.text = callType

        // Populate call logs
        val logContainer = binding.detailCallLogsContainer
        callLogs.forEach { log ->
            // Inflate card view layout
            val cardView = LayoutInflater.from(this)
                .inflate(R.layout.call_log_item, logContainer, false) as CardView
            val dateTextView: TextView = cardView.findViewById(R.id.callDate)
            val timeTextView: TextView = cardView.findViewById(R.id.callTime)
            val durationTextView: TextView = cardView.findViewById(R.id.callDuration)
            val numberTextView: TextView = cardView.findViewById(R.id.callNumber)

            // Set the data to the views
            dateTextView.text =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(log.date))
            timeTextView.text =
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(log.date))
            durationTextView.text = toFormattedDuration(log.duration)
            numberTextView.text = log.number

            // Add the card view to the container
            logContainer.addView(cardView)
        }

        // Log the data
        Log.d("DetailedCallLogActivity", "Number: $number")
        Log.d("DetailedCallLogActivity", "Call Type: $callType")
        callLogs.forEach { log ->
            Log.d(
                "DetailedCallLogActivity",
                "Call Log: ${log.date} - ${toFormattedDuration(log.duration)}"
            )
        }
    }

    private fun toFormattedDuration(duration: Int): String {
        val hours = duration / 3600
        val minutes = (duration % 3600) / 60
        val seconds = duration % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}



