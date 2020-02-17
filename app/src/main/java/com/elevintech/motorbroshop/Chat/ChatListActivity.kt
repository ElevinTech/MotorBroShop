package com.elevintech.motorbroshop.Chat

import android.content.Intent
import android.graphics.Typeface
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
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.row_chat.view.*

class ChatListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getListOfLastMessages(){

        var db = MotorBroDatabase()
        db.getLastMessages {

            displayListOfLastMessages(it)

        }

    }

    private fun displayListOfLastMessages(listOfLasMessages: MutableList<ChatMessage>){

        reycler_view_chats.isNestedScrollingEnabled = true
        var chatListAdapter = GroupAdapter<ViewHolder>()
        val uid = FirebaseAuth.getInstance().uid
        var db = MotorBroDatabase()

        for (lastChatMessage in listOfLasMessages){

            if (lastChatMessage.toId != uid){
                db.getUserById(lastChatMessage.toId){

                    val otherUser = it
                    chatListAdapter.add(chatItem(lastChatMessage, otherUser))

                }
            } else if (lastChatMessage.fromId != uid)  {
                db.getUserById(lastChatMessage.fromId){

                    val otherUser = it
                    chatListAdapter.add(chatItem(lastChatMessage, otherUser))

                }
            }

        }

        reycler_view_chats.adapter = chatListAdapter

    }

    inner class chatItem(val chat: ChatMessage, val chatOtherUser: User): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            viewHolder.itemView.userName.text = chatOtherUser.firstName.capitalize()
            viewHolder.itemView.chatPreview.text = chat.message
            viewHolder.itemView.setOnClickListener {
                val intent = Intent(this@ChatListActivity, ChatLogActivity::class.java)
                intent.putExtra("user", chatOtherUser)
                startActivity(intent)
            }


            // Display the profile image (if they have one)
            if (chatOtherUser.profilePictureUrl != "")
                Glide.with(applicationContext).load(chatOtherUser.profilePictureUrl).into(viewHolder.itemView.imgMainProfile)


            // Display unread message (if the last message is not from user and not yet read)
            val loggedInUser = FirebaseAuth.getInstance().uid
            if (chat.fromId != loggedInUser){
                if (!chat.read){
                    viewHolder.itemView.chatPreview.setTypeface(null, Typeface.BOLD) // TODO: Bold no working always
                    viewHolder.itemView.chatPreview.text = chat.message
//                    viewHolder.itemView.unreadDot.visibility = View.VISIBLE
                }
            }


        }

        override fun getLayout(): Int {
            return R.layout.row_chat

        }
    }
}
