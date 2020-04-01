package com.elevintech.motorbroshop.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.ChatMessage
import com.elevintech.motorbroshop.Model.Customer
import com.elevintech.motorbroshop.Model.Shop
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
    val adapter = GroupAdapter<ViewHolder>()
    var recipientToken = ""
    lateinit var customer: Customer
    lateinit var chatRoomId: String
    lateinit var shop: Shop

    lateinit var customerId: String
    lateinit var shopId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        customerId = intent.getStringExtra("userId")!!
        shopId = intent.getStringExtra("shopId")!!
        chatRoomId = intent.getStringExtra("chatRoomId")!!

        getShop()
        getCustomer()


        btnSendChat.setOnClickListener {
            val message = txtChatMessage.text.toString()
            if ( message != "" && recipientToken != "")
                sendChat()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getCustomer() {
        MotorBroDatabase().getCustomer(customerId){
            customer = it!!

            updateUi()
            getRecipientToken()
        }
    }

    private fun getShop() {
        MotorBroDatabase().getShop(shopId){
            shop = it

            getChats()
        }
    }

    private fun getRecipientToken() {
        MotorBroDatabase().getCustomerDeviceToken(customer.uid) { token ->
            recipientToken = token
        }
    }


    private fun updateUi() {
        Glide.with(this).load(customer.profileImage).into(imgMainProfile)
        profileName.text = customer.firstName.capitalize()
    }


    fun setLastMesssageAsRead(){

        val toId = FirebaseAuth.getInstance().uid!!
        val fromId = customer.uid

//        DateFilipinaDatabase().setLastMessageAsRead(fromId, toId){}
    }

    // need this variable so the pagination value does not get changed when user sends/receives a chat
    var doOnce = false

    fun getChats(){

        if (chatRoomId != ""){
            MotorBroDatabase().getChatRoomMessages(chatRoomId){
                val chatList = it
                displayChats(chatList)
            }
        }

    }

    fun displayChats(chatLogList : MutableList<ChatMessage>){

        recycler_view_chat_logs.adapter = adapter

        for ((index, chatMessage) in chatLogList.withIndex()) {

            // we need to get this to compare dates and determine whether to display date separator or not
            var previousChat = ChatMessage()

            if (index > 0)
                previousChat =  chatLogList[index-1]

            if (chatMessage.fromId == shop.shopId){
                adapter.add(ChatToItem(chatMessage, previousChat))
                adapter.notifyItemInserted(index)
            } else {
                adapter.add(ChatFromItem(chatMessage, previousChat))
                adapter.notifyItemInserted(index)
            }

        }

        recycler_view_chat_logs.scrollToPosition(adapter.itemCount - 1)

    }

    fun sendChat(){

        val createdDate = System.currentTimeMillis() / 1000
        val message = txtChatMessage.text.toString()
        val senderId = shop.shopId
        val receiverId = customer.uid
        val db = MotorBroDatabase()
        val fcmTokenList = arrayListOf(recipientToken)  // list of the device tokens of users who will receive the chat message

        txtChatMessage.setText("")

        if (chatRoomId == ""){

            // create new chat room
            val participants = mapOf("user" to receiverId, "shop" to senderId)
            db.createNewChatRoom ( participants ){ chatRoomId ->

                // save message in chat room
                val chatMessage = ChatMessage(createdDate, senderId, receiverId, message, false, chatRoomId, fcmTokenList)
                db.saveMessageInChatRoom(chatMessage){

                    // save message in last messages
                    db.updateChatRoomLastMessage(chatRoomId, chatMessage){
                        this.chatRoomId = chatRoomId

                        getChats()
                    }

                }

            }

        } else {

            val chatMessage = ChatMessage(createdDate, senderId, receiverId, message, false, chatRoomId!!, fcmTokenList)

            // save message in chat room
            db.saveMessageInChatRoom(chatMessage){

                // save message in last messages
                db.updateChatRoomLastMessage(chatRoomId, chatMessage){

                }

            }
        }

    }

    fun getPreviousChats(){

        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = customer.uid

        var db =  MotorBroDatabase()
        db.getPreviousChatLog(fromId, toId, paginationStartAt){

            val chatList = it

            if (chatList.isNotEmpty()){
                paginationStartAt = chatList.last().createdDate.toInt()

                displayPreviousChats(chatList.asReversed())

            }

//            chatSwipeRefreshLayout.isRefreshing = false

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
        }

    }

    inner class ChatToItem(val chat : ChatMessage, val previous: ChatMessage): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.row_chat_to
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textViewToRow.text = chat.message
            viewHolder.itemView.timeTo.text = chat.getTime()

        }

    }
}
