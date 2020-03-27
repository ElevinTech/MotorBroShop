package com.elevintech.motorbroshop.Model

import com.elevintech.motorbroshop.Utils
import java.io.Serializable

class ChatMessage(val createdDate: Long = 0,
                  val fromId: String = "",
                  val toId: String = "",
                  val message: String = "",
                  val read: Boolean = false,
                  val chatRoomId: String = "") : Serializable {

    fun getDate(): String {
        return Utils().convertMillisecondsToDate(createdDate * 1000, "MMM d yyyy")
    }

    // TODO convert to local time
    fun getTime(): String {
        return Utils().convertMillisecondsToDate(createdDate * 1000, "h:mm a")
    }
}