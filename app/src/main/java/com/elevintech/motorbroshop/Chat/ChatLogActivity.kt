package com.elevintech.motorbroshop.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.ChatMessage
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.row_chat_from.view.*
import kotlinx.android.synthetic.main.row_chat_to.view.*

class ChatLogActivity : AppCompatActivity() {

    var paginationStartAt = 0 // https://www.youtube.com/watch?v=poqTHxtDXwU&t=316s
    lateinit var userSecondary : User
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        Glide.with(this).load(userSecondary.profilePictureUrl).into(imgMainProfile)
        profileName.text = userSecondary.firstName.capitalize()
    }


    fun setLastMesssageAsRead(){

        val toId = FirebaseAuth.getInstance().uid!!
        val fromId = userSecondary.uid

//        DateFilipinaDatabase().setLastMessageAsRead(fromId, toId){}
    }

    // need this variable so the pagination value does not get changed when user sends/receives a chat
    var doOnce = false
    fun getChats(){
        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = userSecondary.uid

        var db = MotorBroDatabase()
        db.getChatLog(fromId, toId){

            val chatList = it
            displayChats(chatList)

            if (chatList.isNotEmpty()){
                if (!doOnce){
                    paginationStartAt = chatList.first().createdDate.toInt()
                    doOnce = true
                }
            }

        }
    }

    fun displayChats(chatLogList : MutableList<ChatMessage>){

        recycler_view_chat_logs.adapter = adapter

        val uid = FirebaseAuth.getInstance().uid

        for ((index, chatMessage) in chatLogList.withIndex()) {

            // we need to get this to compare dates and determine whether to display date separator or not
            var previousChat = ChatMessage()

            if (index > 0)
                previousChat =  chatLogList[index-1]

            if (chatMessage.fromId == uid){
                adapter.add(ChatToItem(chatMessage, previousChat))
            } else {
                adapter.add(ChatFromItem(chatMessage, previousChat))
            }

        }

        recycler_view_chat_logs.scrollToPosition(adapter.itemCount - 1)

    }

    fun sendChat(){

        val createdDate = System.currentTimeMillis() / 1000
        val message = txtChatMessage.text.toString()
        val fromId = FirebaseAuth.getInstance().currentUser?.uid!!
        val toId = userSecondary.uid

        val chatMessage = ChatMessage(createdDate, fromId, toId, message, false)
        val chatMessageRead = ChatMessage(createdDate, fromId, toId, message, true)

        var db = MotorBroDatabase()
        db.saveChatToSender(chatMessageRead){}
        db.saveChatToReceiver(chatMessage){}
        db.saveLastMessageToSender(chatMessageRead){}
        db.saveLastMessageToReceiver(chatMessage){}
        //db.incrementChatBadgeCountOfUser(chatMessage.toId){}

        txtChatMessage.setText("")

    }

    fun getPreviousChats(){

        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = userSecondary.uid

        var db =  MotorBroDatabase()
        db.getPreviousChatLog(fromId, toId, paginationStartAt){

            val chatList = it

            if (chatList.isNotEmpty()){
                paginationStartAt = chatList.last().createdDate.toInt()

                displayPreviousChats(chatList.asReversed())

            }

            chatSwipeRefreshLayout.isRefreshing = false

        }

    }

    fun displayPreviousChats(chatLogList : MutableList<ChatMessage>){

        val uid = FirebaseAuth.getInstance().uid

        for ((index, chatMessage) in chatLogList.withIndex()) {

            var previousChat = ChatMessage()

            if (index > 0)
                previousChat =  chatLogList[index-1]

            if (chatMessage.fromId == uid){
                adapter.add(index, ChatToItem(chatMessage, previousChat))
            } else {
                adapter.add(index, ChatFromItem(chatMessage, previousChat))
            }

        }

        adapter.notifyDataSetChanged()
        // TODO: scroll sa tamang position pagkaload ng paginated messages
        recycler_view_chat_logs.scrollToPosition(7 - 1)
    }



    inner class ChatFromItem(val chat : ChatMessage, val previousChat: ChatMessage): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.row_chat_from
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textViewFromRow.text = chat.message
            viewHolder.itemView.timeFrom.text = chat.getTime()

            println(
                "THIS CHAT: " + chat.createdDate + " || PREVIOUS CHAT: " + previousChat.createdDate + " || MESSAGE: " + chat.message
            )

            // display date separator
            if (chat.getDate() != previousChat.getDate()){
                viewHolder.itemView.txtDateFrom.visibility = View.VISIBLE
                viewHolder.itemView.txtDateFrom.text = chat.getDate()
            }

        }

    }

    inner class ChatToItem(val chat : ChatMessage, val previous: ChatMessage): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.row_chat_to
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textViewToRow.text = chat.message
            viewHolder.itemView.timeTo.text = chat.getTime()

            // display date separator
            if (chat.getDate() != previous.getDate()){
                viewHolder.itemView.txtDateTo.visibility = View.VISIBLE
                viewHolder.itemView.txtDateTo.text = chat.getDate()
            }

            println(
                "THIS CHAT: " + chat.createdDate + " || PREVIOUS CHAT: " + previous.createdDate + " || MESSAGE: " + chat.message
            )
        }

    }
}
