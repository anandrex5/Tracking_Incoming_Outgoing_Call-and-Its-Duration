package com.example.incoming_call



import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.os.IBinder
import android.provider.CallLog
import android.util.Log

class CallService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val incomingNumber = intent?.getStringExtra("incoming_number")
        incomingNumber?.let {
            fetchCallDetails(it)
        }
        return START_NOT_STICKY
    }

    private fun fetchCallDetails(number: String) {
        val cursor: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            "${CallLog.Calls.NUMBER} = ?",
            arrayOf(number),
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.let {
            while (cursor.moveToNext()) {
                val callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
                val callDate = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))
                val callDuration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION))
                val typeString = when (callType) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                Log.d("CallService", "Call details - Type: $typeString, Date: $callDate, Duration: $callDuration seconds")
            }
            cursor.close()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
