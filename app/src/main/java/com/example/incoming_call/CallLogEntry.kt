package com.example.incoming_call


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallLogEntry(
    val type: String,
    val date: Long,
    val duration: Int,
    val number: String
) : Parcelable


//data class CallLogEntry(
//    val type: String,
//    val date: Long,
//    val duration: Int,
//    val number: String
//)