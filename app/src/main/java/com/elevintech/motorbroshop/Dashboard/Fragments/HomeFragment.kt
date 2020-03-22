package com.elevintech.motorbroshop.Dashboard.Fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.elevintech.motorbroshop.Chat.ChatListActivity
import com.elevintech.motorbroshop.Dashboard.DashboardActivity
import com.elevintech.motorbroshop.Database.MotorBroDatabase
import com.elevintech.motorbroshop.Model.User
import com.elevintech.motorbroshop.Model.UserType

import com.elevintech.motorbroshop.R
import com.elevintech.motorbroshop.Shop.ShopActivity
import com.elevintech.motorbroshop.Shop.UpdateShop
import com.elevintech.motorbroshop.Utils
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

        viewShopButton.setOnClickListener {
            val intent = Intent(activity, ShopActivity::class.java)
            intent.putExtra("shopId", user.shopId)
            startActivity(intent)
        }

        editShopButton.setOnClickListener {
            val intent = Intent(activity, UpdateShop::class.java)
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
    private fun setupShop(view: View) {

        if (view.shopName == null) {
            return
        }

        val db = MotorBroDatabase()
        println("shopId is " + user.shopId)
        db.getShop(user.shopId) {
            val shop = it

            view.shopName.text = shop.name

            // GET DATE ESTABLISHED
            if (shop.dateEstablished != "") {
                val date = Utils().convertMillisecondsToDate(shop.dateEstablished, "MMM dd, yyyy")
                view.shopEstablished.text = "Acquired: $date"
            } else {
                view.shopEstablished.text = ""
            }

            // GET EMPLOYEES
            MotorBroDatabase().getShopEmployees(user.shopId){ employeeList ->

                for (employee in employeeList){

                    // instantiate the view for the dialog
                    val viewInflated = layoutInflater.inflate(R.layout.row_employee_dashboard, null)
                    viewInflated.findViewById<TextView>(R.id.employeeName).text = employee.firstName + " " + employee.lastName
                    Glide.with(this).load(employee.profilePictureUrl).into(viewInflated.findViewById(R.id.lastEmployeeImage))
                    view.employeeLayout.addView(viewInflated)


                }

            }

            // GET CUSTOMERS
            MotorBroDatabase().getShopCustomerData(user.shopId){ customerList ->
                val customerCount = customerList.count()
                view.shopCustomersNumber.text = "$customerCount customer(s)"

                // GET FIRST CUSTOMER
                if (customerCount > 0){
                    val firstCustomerBasicData = customerList[0]

                    MotorBroDatabase().getCustomerByUid( firstCustomerBasicData.customerId ){ firstCustomerAllData ->

                        view.lastScannedUserName.text = "${firstCustomerAllData!!.firstName}  ${firstCustomerAllData!!.lastName}"
                        view.lastScannedUserDate.text = Utils().convertMillisecondsToDate((firstCustomerBasicData.dateScanned.toLong() * 1000), "MMM dd, yyyy")
                        println("profile image" + firstCustomerAllData.profileImage)
                        Glide.with(this).load(firstCustomerAllData.profileImage).into(view.lastScannedUserImage)
                    }


                }

            }

            // GET PARTS/SERVICES
            MotorBroDatabase().getShopProducts(user.shopId){ productsList ->
                val productCount = productsList.count()
                view.shopPartsServicesNumber.text = "$productCount parts/services"

                // GET FIRST CUSTOMER
                if (productCount > 0){
                    val latestProduct = productsList[0]
                    view.latestProductName.text = "${latestProduct.brand} ${latestProduct.name}"
                    view.latestProductDate.text = Utils().convertMillisecondsToDate((latestProduct.dateCreated.toLong() * 1000), "MMM dd, yyyy")
                }

            }
        }
    }


}
