package com.elevintech.motorbroshop.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbroshop.Chat.ChatListActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.Model.UserType

import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Shop.ShopActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        user = (activity as DashboardActivity).user

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val db = MotorBroDatabase()

        db.getUserType{ userType ->
            if ( userType == UserType.Type.OWNER) {
                db.getOwner {
                    user = it
                    setupShop(view)
                }
            } else if ( userType == UserType.Type.EMPLOYEE ){
                db.getEmployee {
                    user = it
                    setupShop(view)
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatImageView.setOnClickListener {
            val intent = Intent(context, ChatListActivity::class.java)
            startActivity(intent)
        }

        editShopButton.setOnClickListener {
            val intent = Intent(activity, ShopActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!isAdded) {
            return
        }


    }



    override fun onResume() {
        super.onResume()


    }

    private fun setupShop(view: View) {

        if (view.shopName == null) {
            return
        }

        val db = MotorBroDatabase()
        println("shopId is " + user.shopId)
        db.getShop(user.shopId) {
            val shop = it

            view.shopName.text = shop.name
            //shopName.setText(shop.name)
            if (shop.dateEstablished != "") {

                view.shopEstablished.text = "Acquired: " + shop.dateEstablished
                //shopEstablished.setText("Acquired: " + shop.dateEstablished)
            } else {
                view.shopEstablished.text = ""
            }
        }
    }


}
