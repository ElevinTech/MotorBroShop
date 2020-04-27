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

    private fun getShop() {
        MotorBroDatabase().getShop(shopId){
            shop = it

            // get all chat rooms where shop is a participant of
            getChatRoomOfShop(shop.shopId)
        }
    }

    // ADAPTER REFERENCE - the chat messages and it's current order in the recycler view
    // the position of the chat messages are saved here
    // the adapter uses it to find the current position of a chat message by it's ID used for change it's position in the recycler view
    // (example kapag may bagong chat na na-receive, need idisplay yun sa taas ng recycler view kasi yun yung pinaka-latest)
    val chatListAdapterReference: MutableList<String> = ArrayList()

    fun getChatRoomOfShop(shopId: String){

        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("chat-rooms")
            .whereEqualTo("participants.shop", shopId)
            .orderBy("lastMessage.createdDate", Query.Direction.DESCENDING)

        ref.addSnapshotListener { querysnapshot, e ->

            for ( snapshot in querysnapshot!!.documentChanges){

                noDataLayout.visibility = View.GONE
                reycler_view_chats.visibility = View.VISIBLE

                if ( snapshot.type == DocumentChange.Type.ADDED ){

                    // get the chats
                    val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                    chatRoom.id = snapshot.document.id

                    // display in the recycler view
                    chatListAdapter.add(ChatItem(chatRoom, chatRoom.participants["user"]!!))

                    // add to adapter reference
                    chatListAdapterReference.add(chatRoom.id)

                }

                if ( snapshot.type == DocumentChange.Type.MODIFIED  ){

                    // get new chat
                    val chatRoom = snapshot.document.toObject(ChatRoom::class.java)!!
                    val newMessage = chatRoom.lastMessage.message
                    chatRoom.id = snapshot.document.id

                    // get the chat item position based from adapter reference (we will be moving it to the top of the recycler view as it is the new latest message)
                    val oldPosition = chatListAdapterReference.indexOf(chatRoom.id)
                    val newPosition = 0

                    // update the adapter reference, move it to the first position as well
                    chatListAdapterReference.remove(chatRoom.id)
                    chatListAdapterReference.add(0, chatRoom.id)

                    // change the value of the chat message text
                    chatListAdapter.notifyItemChanged(oldPosition, "$newMessage")

                    // move to the top of the recyclerview
                    chatListAdapter.notifyItemMoved(oldPosition, newPosition) /* move the chat on the first row */

                }
            }

            if (querysnapshot.isEmpty) {
                noDataLayout.visibility = View.VISIBLE
                reycler_view_chats.visibility = View.GONE
            }

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
