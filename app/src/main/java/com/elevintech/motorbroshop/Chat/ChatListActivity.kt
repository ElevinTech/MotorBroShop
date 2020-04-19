package com.elevintech.motorbroshop.Chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.ChatRoom
import com.elevintech.motorbroshop.Model.Shop
import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Utils
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    }

    override fun onResume() {
        super.onResume()

        chatListAdapter.clear()
        chatListAdapterReference.clear()
        reycler_view_chats.adapter = chatListAdapter

        getShop()
    }

    // ADAPTER REFERENCE
    // the position of the chat messages are saved here
    // the adapter uses it to find the current position of a chat message by it's ID to change it's order
    // (kapag example yung pangatlong message ay nag bago so need yun ilagay sa taas na position kasi yun na yung latest, hahanapin yung current position nun sa list na to base sa ID nya)
    val chatListAdapterReference: MutableList<String> = ArrayList()

    fun getChatRoomOfShop(shopId: String){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-rooms")
            .whereEqualTo("participants.shop", shopId)
            .orderBy("lastMessage.createdDate", Query.Direction.DESCENDING)

        ref.addSnapshotListener { querysnapshot, e ->

            for ( snapshot in querysnapshot!!.documentChanges){

                if ( snapshot.type == DocumentChange.Type.ADDED ){

                    // GET THE LATEST CHATS
                    val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                    chatRoom.id = snapshot.document.id

                    // ADD TO ADAPTER + ADAPTER REFERENCE
                    chatListAdapter.add(ChatItem(chatRoom, chatRoom.participants["user"]!!))
                    chatListAdapterReference.add(chatRoom.id)

                }

                if ( snapshot.type == DocumentChange.Type.MODIFIED  ){

                    // GET THE LATEST CHAT
                    val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                    val newMessage = chatRoom.lastMessage.message
                    chatRoom.id = snapshot.document.id

                    // THE ADAPTER REFERENCE USED HERE
                    val oldPosition = chatListAdapterReference.indexOf(chatRoom.id)
                    val newPosition = 0 /* new index position 0 = first position in the recycler view */

                    // MOVE THE CHAT ITEM TO THE FIRST POSITION OF THE ADAPTER REFERENCE
                    chatListAdapterReference.remove(chatRoom.id)
                    chatListAdapterReference.add(0, chatRoom.id)

                    // UPDATE CHAT MESSAGE
                    chatListAdapter.notifyItemChanged(oldPosition, "$newMessage")

                    // MOVE THE CHAT ITEM TO THE FIRST POSITION OF THE ADAPTER
                    chatListAdapter.notifyItemMoved(oldPosition, newPosition) /* move the chat on the first row */

                }
            }

        }
    }

    private fun getShop() {
        MotorBroDatabase().getShop(shopId){
            shop = it

            // get all chat rooms where shop is a participant of
            getChatRoomOfShop(shop.shopId)
        }
    }



    inner class ChatItem(val chatRoom: ChatRoom, val user: String): Item<ViewHolder>() {

        // UPDATE CHAT MESSAGE
        override fun bind(viewHolder: ViewHolder, position: Int, payloads: List<Any>) {

            if (payloads.isNotEmpty()){

                val newMessage = payloads[0] as String
                viewHolder.itemView.chatPreview.text = newMessage
                viewHolder.itemView.unreadDot.visibility = View.VISIBLE
                viewHolder.itemView.chatDate.text = Utils().getCurrentTime()

            } else {
                super.bind(viewHolder, position, payloads)
            }
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            MotorBroDatabase().getCustomerById(user){ chatOtherUser ->
                viewHolder.itemView.userName.text = chatOtherUser.firstName.capitalize()

                if (chatRoom.lastMessage.fromId == shopId){
                    viewHolder.itemView.chatPreview.text = "You: " + chatRoom.lastMessage.message
                } else {
                    viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message
                }

                viewHolder.itemView.chatPreview.text = chatRoom.lastMessage.message
                viewHolder.itemView.chatDate.text = Utils().convertMillisecondsToDate(chatRoom.lastMessage.createdDate * 1000, "MMM dd - hh:mm a")

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

        }

        override fun getLayout(): Int {
            return R.layout.row_chat

        }
    }
}
