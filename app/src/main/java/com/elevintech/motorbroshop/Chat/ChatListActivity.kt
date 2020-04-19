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
    val chatListAdapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        shopId = intent.getStringExtra("shopId")!!

        btnBack.setOnClickListener {
            finish()
        }

        reycler_view_chats.isNestedScrollingEnabled = true
        reycler_view_chats.adapter = chatListAdapter

    }

    override fun onResume() {
        super.onResume()

        getShop()
    }

    private fun getShop() {
        MotorBroDatabase().getShop(shopId){
            shop = it

            // get all chat rooms where shop is a participant of
            getShopChatRooms()
        }
    }

    val latestMessagesMap = HashMap<String, ChatRoom>()
    private fun refreshRecyclerViewMessages(){
        chatListAdapter.clear()
        latestMessagesMap.values.forEachIndexed { index, chatRoom ->
            MotorBroDatabase().getCustomerById(chatRoom.participants["user"]!!){
                chatListAdapter.add( ChatItem(chatRoom, it) )
                chatListAdapter.notifyItemInserted(index)
            }

        }
    }

    private fun getShopChatRooms(){

        var db = MotorBroDatabase()
        db.getChatRoomOfShop(shopId) {

            displayListOfLastMessages(it)

        }

    }

    private fun displayListOfLastMessages(chatRoomList: MutableList<ChatRoom>){

        for (chatRoom in chatRoomList.reversed()){
            latestMessagesMap[chatRoom.id] = chatRoom
        }

        refreshRecyclerViewMessages()

    }

    inner class ChatItem(val chatRoom: ChatRoom, val chatOtherUser: Customer): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.userName.text = chatOtherUser.firstName.capitalize()

            if (chatRoom.lastMessage.fromId == shopId){
                viewHolder.itemView.chatPreview.text = "You: " + chatRoom.lastMessage.message
            } else {
                viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message
            }

            viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message

            viewHolder.itemView.setOnClickListener {
                val intent = Intent(this@ChatListActivity, ChatLogActivity::class.java)
                intent.putExtra("customerId", chatOtherUser.uid)
                intent.putExtra("chatRoomId", chatRoom.lastMessage.chatRoomId)
                intent.putExtra("shopId", shop.shopId)
                startActivity(intent)
            }


            // Display the profile image (if they have one)
            if (chatOtherUser.profileImage != "")
                Glide.with(applicationContext).load(chatOtherUser.profileImage).into(viewHolder.itemView.imgMainProfile)

            if (chatRoom.lastMessage.toId == shopId) {
                if(chatRoom.lastMessage.read == false){
                    viewHolder.itemView.unreadDot.visibility = View.VISIBLE
                }
            }


        }

        override fun getLayout(): Int {
            return R.layout.row_chat

        }
    }
}
