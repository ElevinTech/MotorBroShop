package com.elevintech.motorbroshop.Dashboard.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.Model.UserType

import com.elevintech.motorbroshop.R
import kotlinx.android.synthetic.main.fragment_home.*

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = MotorBroDatabase()

        db.getUserType{ userType ->

            if ( userType == UserType.Type.OWNER) {

                db.getOwner {
                    println("its an owner!")
                    println("user is " + it.firstName)
                    user = it
                    setupShop()
                }

            } else if ( userType == UserType.Type.EMPLOYEE ){

                db.getEmployee {
                    user = it
                    setupShop()
                }

            }

        }



//        shopUser.text = user.firstName + " " + user.lastName

    }

    private fun setupShop() {
        val db = MotorBroDatabase()
        println("shopId is " + user.shopId)
        db.getShop(user.shopId) {
            val shop = it

            shopName.setText(shop.name)

            if (shop.dateEstablished != "") {
                shopEstablished.setText("Acquired: " + shop.dateEstablished)
            } else {
                shopEstablished.setText("")
            }


        }
    }


}
