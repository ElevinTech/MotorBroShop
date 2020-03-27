package com.elevintech.motorbroshop.Chat

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.*
import com.elevintech.motorbroshop.R
import com.google.firebase.auth.FirebaseAuth
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.row_chat.view.*

class ChatListActivity : AppCompatActivity() {

    var shopId = ""
    lateinit var shop: Shop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        shopId = intent.getStringExtra("shopId")!!

        btnBack.setOnClickListener {
            finish()
        }

        MotorBroDatabase().getShop(shopId){
            shop = it

            // get all chat rooms where shop is a participant of
            getShopChatRooms()
        }


    }

    private fun getShopChatRooms(){

        var db = MotorBroDatabase()
        db.getChatRoomOfShop(shopId) {

            displayListOfLastMessages(it)

        }

    }

    private fun displayListOfLastMessages(chatRoomList: MutableList<ChatRoom>){

        reycler_view_chats.isNestedScrollingEnabled = true

        val chatListAdapter = GroupAdapter<ViewHolder>()

        for (chatRoom in chatRoomList){

            MotorBroDatabase().getCustomerById(chatRoom.participants["user"]!!){
                chatListAdapter.add( ChatItem(chatRoom, it) )
            }

        }

        reycler_view_chats.adapter = chatListAdapter

    }

    inner class ChatItem(val chatRoom: ChatRoom, val chatOtherUser: Customer): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.userName.text = chatOtherUser.firstName.capitalize()
            viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message
            viewHolder.itemView.setOnClickListener {
                val intent = Intent(this@ChatListActivity, ChatLogActivity::class.java)
                intent.putExtra("customer", chatOtherUser)
                intent.putExtra("chatRoomId", chatRoom.lastMessage.chatRoomId)
                intent.putExtra("shop", shop)
                startActivity(intent)
            }


            // Display the profile image (if they have one)
            if (chatOtherUser.profileImage != "")
                Glide.with(applicationContext).load(chatOtherUser.profileImage).into(viewHolder.itemView.imgMainProfile)


            // Display unread message (if the last message is not from user and not yet read)
            val loggedInUser = FirebaseAuth.getInstance().uid
            if (chatRoom.lastMessage.fromId != loggedInUser){
                if (!chatRoom.lastMessage.read){
                    viewHolder.itemView.chatPreview.setTypeface(null, Typeface.BOLD) // TODO: Bold not working always
                    viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message
//                    viewHolder.itemView.unreadDot.visibility = View.VISIBLE
                }
            }


        }

        override fun getLayout(): Int {
            return R.layout.row_chat

        }
    }
}
