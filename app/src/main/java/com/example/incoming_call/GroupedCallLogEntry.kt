package com.example.incoming_call

data class GroupedCallLogEntry(
    val number: String,
    val callType: String,
    val callLogs: List<CallLogEntry>
)
