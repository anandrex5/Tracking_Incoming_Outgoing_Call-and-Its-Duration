package com.example.incoming_call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                Log.d("CallReceiver", "Incoming call from: $incomingNumber")

                // Start a service to fetch call details
                val serviceIntent = Intent(context, CallService::class.java)
                serviceIntent.putExtra("incoming_number", incomingNumber)
                context?.startService(serviceIntent)
            }
        }
    }
}
