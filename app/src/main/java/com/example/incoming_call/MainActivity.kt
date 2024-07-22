package com.example.incoming_call

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.incoming_call.databinding.ActivityMainBinding

//v3 - Working -


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val callLogs = mutableListOf<CallLogEntry>()
    private val groupedCallLogs = mutableListOf<GroupedCallLogEntry>()
    private lateinit var expandableCallLogAdapter: ExpandableCallLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expandableCallLogAdapter = ExpandableCallLogAdapter(groupedCallLogs)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expandableCallLogAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchCallLogs()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), 1)
        } else {
            fetchCallLogs()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCallLogs()
        }
    }

    private fun fetchCallLogs() {
        callLogs.clear()
        groupedCallLogs.clear()
        binding.swipeRefreshLayout.isRefreshing = true

        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.let {
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)

            while (cursor.moveToNext()) {
                val callType = if (typeIndex >= 0) it.getInt(typeIndex) else -1
                val callDate = if (dateIndex >= 0) it.getLong(dateIndex) else 0L
                val callDuration = if (durationIndex >= 0) it.getInt(durationIndex) else 0
                val number = if (numberIndex >= 0) it.getString(numberIndex) else "Unknown"

                val typeString = when (callType) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                callLogs.add(CallLogEntry(typeString, callDate, callDuration, number))
            }
            cursor.close()
        }

        groupCallLogs()
        expandableCallLogAdapter.notifyDataSetChanged()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun groupCallLogs() {
        val groupedMap = callLogs.groupBy { Pair(it.number, it.type) }
        groupedMap.forEach { (key, value) ->
            groupedCallLogs.add(GroupedCallLogEntry(key.first, key.second, value))
        }
    }

    private fun Int.toFormattedDuration(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}




//V2 working
/*class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val callLogs = mutableListOf<CallLogEntry>()
    private lateinit var callLogAdapter: CallLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callLogAdapter = CallLogAdapter(callLogs) { callLogEntry ->
            Log.d("MainActivity", "Clicked on: $callLogEntry")
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = callLogAdapter
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), 1)
        } else {
            fetchCallLogs()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCallLogs()
        }
    }

    private fun fetchCallLogs() {
        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC"
        )

        cursor?.let {
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)

            while (cursor.moveToNext()) {
                val callType = if (typeIndex >= 0) it.getInt(typeIndex) else -1
                val callDate = if (dateIndex >= 0) it.getLong(dateIndex) else 0L
                val callDuration = if (durationIndex >= 0) it.getInt(durationIndex) else 0
                val callNumber = if (numberIndex >= 0) it.getString(numberIndex) ?: "" else ""

                val typeString = when (callType) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                callLogs.add(CallLogEntry(typeString, callDate, callDuration, callNumber))
            }
            cursor.close()
            callLogAdapter.notifyDataSetChanged()
        }
    }
}*/
